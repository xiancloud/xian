package info.xiancloud.dao.group.unit;

import com.alibaba.fastjson.JSON;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.group.DaoGroup;
import info.xiancloud.dao.jdbc.pool.DatasourceConfigReader;
import info.xiancloud.dao.jdbc.pool.PoolFactory;
import info.xiancloud.dao.jdbc.sql.AbstractAction;
import info.xiancloud.dao.jdbc.sql.Action;
import info.xiancloud.dao.jdbc.sql.ISelect;
import info.xiancloud.dao.jdbc.transaction.Transaction;
import info.xiancloud.dao.jdbc.transaction.TransactionFactory;

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

    /**
     *
     * @param request the request object.
     * @param handler the unit response consumer, this handler must be executed asynchronously.
     */
    @Override
    public final void execute(UnitRequest request, NotifyHandler handler) {
        handler.callback(execute(request));
    }

    private UnitResponse execute(UnitRequest msg) {
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
