package info.xiancloud.dao.core.units;

import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.DaoGroup;
import info.xiancloud.dao.core.action.AbstractSqlAction;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.select.ISelect;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.pool.PoolFactory;
import info.xiancloud.dao.core.transaction.TransactionFactory;
import io.reactivex.Observable;

import java.util.Map;

/**
 * Dao unit template.
 *
 * @author happyyangyuan
 */
public abstract class DaoUnit implements Unit {

    @Override
    public final Input getInput() {
        return new Input();
    }

    private void bareError(UnitRequest request) {
        request.setArgMap(StringUtil.underlineToCamel(request.getArgMap()));
    }

    /**
     * @param request the request object.
     * @param handler the unit response consumer, this handler must be executed asynchronously.
     */
    @Override
    public final void execute(UnitRequest request, Handler<UnitResponse> handler) {
        SqlAction[] sqlActions = getActions();
        bareError(request);
        boolean readOnly = readOnly(sqlActions, request) || request.getContext().isReadyOnly();
        UnitResponse[] lastResponse = {null};
        TransactionFactory.getTransaction(request.getContext().getMsgId(), readOnly)
                .subscribe(transaction -> Observable.fromArray(sqlActions)
                        .doOnNext(action ->
                                action.execute(DaoUnit.this, request.getArgMap(), transaction.getConnection())
                                        .doOnSuccess(unitResponse -> {
                                            if (!unitResponse.succeeded()) {
                                                //deal with sql failure
                                                LOG.error(unitResponse);
                                                throw new Exception("sql execution failure: " + unitResponse.getMessage());
                                                //fixme it is bad to throw exception to end the loop.
                                            } else {
                                                lastResponse[0] = unitResponse;
                                            }
                                        }))
                        .doOnComplete(() -> transaction.commit().subscribe(() -> handler.handle(lastResponse[0])))
                        .doOnError(error -> transaction.rollback().subscribe(
                                () -> handler.handle(UnitResponse.createError(DaoGroup.CODE_DB_ERROR, error, null))
                        ))
                        .subscribe())
        ;
    }

    /**
     * provides sql actions here
     *
     * @return sql actions array
     */
    abstract public SqlAction[] getActions();

    private boolean readOnly(SqlAction[] sqlActions, UnitRequest request) {
        //优先级   !SelectAction > request.readonly() > meta.isReadonly()
        for (SqlAction action : sqlActions) {
            if (!(action instanceof ISelect)) {
                return false;
            }
        }
        return request.getContext().isReadyOnly() || getMeta().isReadonly();
    }

    /**
     * 打印sql语句，它不会将sql执行，只是打印sql语句。
     * 仅供内部测试使用
     *
     * @param daoUnitClass unit class
     * @param map          parameter map
     */
    public static void logSql(Class daoUnitClass, Map<String, Object> map) {
        XianConnection connection = PoolFactory.getPool().getMasterDatasource().getConnection().blockingGet();
        DaoUnit daoUnit;
        try {
            daoUnit = (DaoUnit) daoUnitClass.newInstance();
            for (SqlAction action : daoUnit.getActions()) {
                ((AbstractSqlAction) action).setConnection(connection);
                ((AbstractSqlAction) action).setMap(map);
                action.logSql(map);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        PoolFactory.getPool().destroyPoolIfNot();
    }

    /**
     * 执行sql
     * 仅供内部测试使用
     *
     * @param unitClass unit class
     * @param map       parameter map
     */
    public static void test(Class unitClass, Map<String, Object> map) {
        try {
            DaoUnit daoUnit = (DaoUnit) unitClass.newInstance();
            UnitRequest msg = new UnitRequest(map);
            daoUnit.execute(msg, unitResponse -> {
                System.out.println("dao output>>>>>>>>  " + unitResponse);
            });
            PoolFactory.getPool().destroyPoolIfNot();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}