package info.xiancloud.plugin.dao.core.group.unit;

import com.alibaba.fastjson.JSON;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.dao.core.group.DaoGroup;
import info.xiancloud.plugin.dao.core.jdbc.pool.DatasourceConfigReader;
import info.xiancloud.plugin.dao.core.jdbc.pool.PoolFactory;
import info.xiancloud.plugin.dao.core.jdbc.sql.AbstractAction;
import info.xiancloud.plugin.dao.core.jdbc.sql.Action;
import info.xiancloud.plugin.dao.core.jdbc.sql.ISelect;
import info.xiancloud.plugin.dao.core.jdbc.transaction.Transaction;
import info.xiancloud.plugin.dao.core.jdbc.transaction.TransactionFactory;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;
import info.xiancloud.plugin.util.thread.MsgIdHolder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * dao unit template
 *
 * @author happyyangyuan
 */
public abstract class DaoUnit implements Unit {

    private static ThreadLocal<Action[]> actions = new ThreadLocal<>();//xian内的unit都是单例的,不能用成员变量，只能用threadLocal线程变量了
    private static ThreadLocal<Transaction> transaction = new ThreadLocal<>();

    @Override
    public final Input getInput() {
        return new Input();
    }

    private void bareError(UnitRequest msg) {
        msg.setArgMap(StringUtil.underlineToCamel(msg.getArgMap()));
    }

    private void init(UnitRequest msg) {
        actions.set(getActions());
        bareError(msg);
        LOG.debug("daoUnit尝试获取/创建事务");
        transaction.set(TransactionFactory.getTransaction(MsgIdHolder.get(), readOnly(msg)));
    }

    public Action[] getLocalActions() {
        return actions.get();
    }

    private void destroy() {
        actions.set(null);
        if (transaction.get() != null) {
            transaction.get().close();
            transaction.set(null);
        }
    }

    @Override
    public final UnitResponse execute(UnitRequest msg) {
        try {
            init(msg);
            transaction.get().begin();
            List<UnitResponse> unitResponseObjects = new ArrayList<>();
            for (Action action : getProxiedActions()) {
                UnitResponse unitResponseObject = action.execute(this, msg.getArgMap(), transaction.get().getConnection());
                if (Group.CODE_SUCCESS.equals(unitResponseObject.getCode())) {
                    unitResponseObjects.add(unitResponseObject);
                } else {//db插件内如果返回的code不是SUCCESS,目前的实现是一律回滚事务
                    transaction.get().rollback();
                    return unitResponseObject;
                }
            }
            transaction.get().commit();
            return unitResponseObjects.get(unitResponseObjects.size() - 1);
        } catch (Throwable t) {
            LOG.error(t);
            transaction.get().rollback();
            return UnitResponse.error(DaoGroup.CODE_DB_ERROR, t, null);
        } finally {
            destroy();
        }
    }

    private List<Action> getProxiedActions() {
        List<Action> proxies = new ArrayList<>();
        for (Action action : getLocalActions()) {
            proxies.add(Action.createActionProxy(action));
        }
        return proxies;
    }

    abstract public Action[] getActions();

    private boolean readOnly(UnitRequest request) {
        //优先级   !SelectAction > Xian.readonly() > meta.isReadonly()
        for (Action action : getLocalActions()) {
            if (!(action instanceof ISelect)) {
                return false;
            }
        }
        return request.getContext().isReadyOnly() || getMeta().isReadonly();
    }

    /**
     * 打印sql语句，它不会将sql执行，只是打印sql语句。
     * 仅供内部测试使用
     */
    public static void logSql(Class daoUnitClass, Map map) {
        try (
                Connection conn = DriverManager.getConnection(DatasourceConfigReader.getWriteUrl(), DatasourceConfigReader.getWriteUser(), DatasourceConfigReader.getWritePwd())
        ) {
            DaoUnit daoUnit = (DaoUnit) daoUnitClass.newInstance();
            for (Action action : daoUnit.getActions()) {
                ((AbstractAction) action).setConnection(conn);
                ((AbstractAction) action).setMap(map);
                /*((AbstractAction) action).create();*/
                action.logSql(map);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 执行sql
     * 仅供内部测试使用
     */
    public static void test(Class unitClass, Map<String, Object> map) {
        try {
            DaoUnit daoUnit = (DaoUnit) unitClass.newInstance();
            UnitRequest msg = new UnitRequest(map);
            System.out.println("输出>>>>>>>>  " + JSON.toJSONString(daoUnit.execute(msg)));
            PoolFactory.getPool().destroyPool();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
