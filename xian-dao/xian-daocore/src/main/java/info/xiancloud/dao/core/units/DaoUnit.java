package info.xiancloud.dao.core.units;

import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.ExceptionWithUnitResponse;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.action.AbstractSqlAction;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.select.ISelect;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.pool.PoolFactory;
import info.xiancloud.dao.core.transaction.TransactionFactory;
import info.xiancloud.dao.core.transaction.XianTransaction;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
        final SqlAction[] sqlActions = getActions();
        bareError(request);
        final boolean readOnly = readOnly(sqlActions, request) || request.getContext().isReadyOnly();
        final AtomicBoolean transactional = new AtomicBoolean(false);
        final UnitResponse[] tempUnitResponse = new UnitResponse[]{null};
        final XianTransaction[] tempTransaction = new XianTransaction[]{null};
        TransactionFactory
                .getTransaction(request.getContext().getMsgId(), readOnly)
                .flatMap(transaction -> {
                    tempTransaction[0] = transaction;
                    transactional.set(isTransactional(sqlActions, transaction));
                    if (transactional.get()) {
                        //business layer has begun the transaction, here we reentrant it.
                        return transaction.begin().toSingle(() -> transaction);
                    } else {
                        //if transaction is not begun by business layer, here we do not begin the transaction
                        return Single.just(transaction);
                    }
                })
                .flatMap(transaction -> Flowable.fromArray(sqlActions)
                        .flatMapSingle(action -> action.execute(this, request.getArgMap(), transaction.getConnection(), request.getContext().getMsgId()))
                        .reduce(UnitResponse.succeededSingleton()
                                , (unitResponse, unitResponse2) -> {
                                    if (unitResponse2.succeeded()) {
                                        return unitResponse2;
                                    } else {
                                        throw new ExceptionWithUnitResponse(unitResponse2);
                                    }
                                })
                        .flatMapCompletable(unitResponse -> {
                            tempUnitResponse[0] = unitResponse;
                            if (transactional.get()) {
                                return transaction.commit();
                            } else {
                                return Completable.complete();
                            }
                        })
                        .toSingle(() -> tempUnitResponse[0])
                        .onErrorResumeNext(error -> {
                            ExceptionWithUnitResponse exceptionWithUnitResponse;
                            if (error instanceof ExceptionWithUnitResponse) {
                                exceptionWithUnitResponse = (ExceptionWithUnitResponse) error;
                            } else {
                                exceptionWithUnitResponse = new ExceptionWithUnitResponse(UnitResponse.createException(error));
                            }
                            if (transactional.get()) {
                                //if transaction is begun then rollback here
                                return transaction.rollback().andThen(Single.just(exceptionWithUnitResponse.getUnitResponse()));
                            } else {
                                //if no transaction is begun, no need transaction rollback.
                                return Single.just(exceptionWithUnitResponse.getUnitResponse());
                            }
                        }))
                //close the transaction asynchronously is ok and better
                .doFinally(() -> tempTransaction[0].close().subscribe())
                .subscribe(unitResponse -> {
                    unitResponse.getContext().setMsgId(request.getContext().getMsgId());
                    handler.handle(unitResponse);
                });
    }

    /**
     * provides sql actions here
     *
     * @return sql actions array
     */
    abstract public SqlAction[] getActions();

    /**
     * judge whether we need to begin the transaction
     */
    private boolean isTransactional(SqlAction[] sqlActions, XianTransaction transaction) {
        for (SqlAction sqlAction : sqlActions) {
            if (!(sqlAction instanceof ISelect)) {
                return true;
            }
        }
        return transaction.isBegun();
    }

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