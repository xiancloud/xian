package info.xiancloud.dao.async.postgresql;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Pair;
import info.xiancloud.core.util.thread.MsgIdHolder;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.insert.BatchInsertAction;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.sqlresult.*;
import info.xiancloud.dao.core.sql.BaseSqlDriver;
import info.xiancloud.dao.core.utils.BasicSqlBuilder;
import info.xiancloud.dao.core.utils.PgPatternUtil;
import io.reactiverse.reactivex.pgclient.PgIterator;
import io.reactiverse.reactivex.pgclient.Row;
import io.reactiverse.reactivex.pgclient.Tuple;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * postgresql sql executor implementation
 *
 * @author happyyangyuan
 */
public class PgSqlDriver extends BaseSqlDriver {
    private io.reactiverse.reactivex.pgclient.PgConnection pgConnection0;

    @Override
    public Single<SingleInsertionResult> insert(String patternSql, Map<String, Object> params) {
        Tuple tuple = Tuple.tuple();
        for (Object o : params.values()) {
            tuple.addValue(o);
        }
        final String msgId = MsgIdHolder.get();
        return pgConnection0
                .rxPreparedQuery(preparedSql(patternSql), tuple)
                /*.observeOn(new XianRxJava2Scheduler().setMsgId(msgId))
                todo, it seems that if we use reactive-pg-client, we have to use vertx-worker-thread.
                todo, because reactive-pg-client uses the vertx-work-thread context to hold transaction status.
                 */
                .flatMap(pgRowSet -> Single.just(new SingleInsertionResult()
                        .setCount(pgRowSet.rowCount())
                        //todo here is no generated id
                        .setId(null)
                ))
                ;
    }

    @Override
    protected BaseSqlDriver setConnection0(XianConnection connection) {
        pgConnection0 = ((PgConnection) connection).getPgConnection0();
        return this;
    }

    @Override
    public String preparedSql(String patternSql) {
        if (preparedSql == null) {
            preparedSql = PgPatternUtil.getPreparedSql(patternSql);
        }
        return preparedSql;
    }

    @Override
    public Pair<String, Object[]> preparedBatchInsertionSql(BatchInsertAction batchInsertAction) {
        if (preparedSql == null) {
            Pair<String, Object[]> preparedSqlAndValues = BasicSqlBuilder.buildPgBatchInsertPreparedSQL(
                    batchInsertAction.getTableName(), batchInsertAction.getCols(), batchInsertAction.getValues());
            preparedSql = preparedSqlAndValues.fst;
            preparedParams = preparedSqlAndValues.snd;
        }
        return Pair.of(preparedSql, preparedParams);
    }

    @Override
    public UnitResponse handleException(Throwable exception, SqlAction sqlAction) {
        String fullActualSql = sqlAction.getFullSql();
        UnitResponse response = UnitResponse.createException(exception, "sql failure: " + fullActualSql);
///      todo check this exception.
//        if (exception instanceof SQLException) {
//            switch (((SQLException) exception).getErrorCode()) {
//                //fixme, this is for mysql only
//                /*Error: 1062 SQLSTATE: 23000 (ER_DUP_ENTRY)
//                  Message: Duplicate entry '%s' for key %d */
//                case 1062:
//                    response = UnitResponse.create(DaoGroup.CODE_REPETITION_NOT_ALLOWED, fullActualSql, exception.getLocalizedMessage());
//                    break;
//                default:
//                    response = UnitResponse.createError(DaoGroup.CODE_SQL_ERROR, fullActualSql, "执行sql语句出现问题");
//            }
//        }
        return response;
    }

    @Override
    public Single<UpdatingResult> update(String patternSql, Map<String, Object> map) {
        LOG.info("=================== execution update sql driver");
        final String msgId = MsgIdHolder.get();
        return pgConnection0
                .rxPreparedQuery(preparedSql(patternSql), tupleFromArray(preparedParams(patternSql, map)))
                /*.observeOn(new XianRxJava2Scheduler().setMsgId(msgId))
                todo, it seems that if we use reactive-pg-client, we have to use vertx-worker-thread.
                todo, because reactive-pg-client uses the vertx-work-thread context to hold transaction status.
                 */
                .map(pgRowSet -> {
                    LOG.info("===================  parse updation result set: " + map);
                    return new UpdatingResult().setCount(pgRowSet.rowCount());
                })
                ;
    }

    @Override
    public Single<DeletionResult> delete(String patternSql, Map<String, Object> map) {
        LOG.info("=================== execution delete sql driver");
        final String msgId = MsgIdHolder.get();
        return pgConnection0
                .rxPreparedQuery(preparedSql(patternSql), tupleFromArray(preparedParams(patternSql, map)))
                /*.observeOn(new XianRxJava2Scheduler().setMsgId(msgId))
                todo, it seems that if we use reactive-pg-client, we have to use vertx-worker-thread.
                todo, because reactive-pg-client uses the vertx-work-thread context to hold transaction status.
                 */
                .map(pgRowSet -> {
                    LOG.info("===================  parse deletion result set: " + map);
                    return new DeletionResult().setCount(pgRowSet.rowCount());
                })
                ;
    }

    @Override
    public Single<BatchInsertionResult> batchInsert(BatchInsertAction batchInsertAction) {
        Pair<String, Object[]> pair = preparedBatchInsertionSql(batchInsertAction);
        final String msgId = MsgIdHolder.get();
        return pgConnection0
                .rxPreparedQuery(pair.fst, tupleFromArray(pair.snd))
                /*.observeOn(new XianRxJava2Scheduler().setMsgId(msgId))
                todo, it seems that if we use reactive-pg-client, we have to use vertx-worker-thread.
                todo, because reactive-pg-client uses the vertx-work-thread context to hold transaction status.
                 */
                .map(pgRowSet -> new BatchInsertionResult().setCount(pgRowSet.rowCount()))
                ;
    }

    @Override
    public Single<String[]> queryCols(String tableName) {
        final String msgId = MsgIdHolder.get();
        return pgConnection0
                .rxPreparedQuery("SELECT * FROM " + tableName + " WHERE 1>2 ")
                /*.observeOn(new XianRxJava2Scheduler().setMsgId(msgId))
                todo, it seems that if we use reactive-pg-client, we have to use vertx-worker-thread.
                todo, because reactive-pg-client uses the vertx-work-thread context to hold transaction status.
                 */
                .map(pgRowSet -> {
                    List<String> cols = pgRowSet.columnsNames();
                    return cols.toArray(new String[cols.size()]);
                })
                ;
    }

    @Override
    public Single<String> getIdCol(String tableName) {
        final String primaryKeyNameAlias = "primarykey",
                msgId = MsgIdHolder.get();
        return pgConnection0
                .rxQuery(
                        " SELECT a.attname AS " + primaryKeyNameAlias +
                                " FROM   pg_index i " +
                                " JOIN   pg_attribute a ON a.attrelid = i.indrelid " +
                                "                      AND a.attnum = ANY(i.indkey) " +
                                " WHERE  i.indrelid = '" + tableName + "'::regclass " +
                                " AND    i.indisprimary; ")
                /*.observeOn(new XianRxJava2Scheduler().setMsgId(msgId))
                todo, it seems that if we use reactive-pg-client, we have to use vertx-worker-thread.
                todo, because reactive-pg-client uses the vertx-work-thread context to hold transaction status.
                 */
                .map(pgRowSet -> pgRowSet.iterator().next().getString(primaryKeyNameAlias))
                ;
    }

    @Override
    public Completable buildTableMetaData(Table table) {
        //fixme not supported
        throw new RuntimeException("not supported yet.");
    }

    @Override
    public Single<RecordsListSelectionResult> select(String patternSql, Map<String, Object> map) {
        final String msgId = MsgIdHolder.get();
        return Single.fromCallable(() -> preparedParams(patternSql, map).length > 0)
                .flatMap(prepared -> {
                    if (prepared) {
                        return pgConnection0
                                .rxPreparedQuery(preparedSql(patternSql), tupleFromArray(preparedParams(patternSql, map)))
                                /* .observeOn(new XianRxJava2Scheduler().setMsgId(msgId))
                                   todo, it seems that if we use reactive-pg-client, we have to use vertx-worker-thread.
                                   todo, because reactive-pg-client uses the vertx-work-thread context to hold transaction status.
                                */
                                ;
                    } else {
                        return pgConnection0
                                .rxQuery(patternSql)
                                /* .observeOn(new XianRxJava2Scheduler().setMsgId(msgId))
                                   todo, it seems that if we use reactive-pg-client, we have to use vertx-worker-thread.
                                   todo, because reactive-pg-client uses the vertx-work-thread context to hold transaction status.
                                */
                                ;
                    }
                })
                .map(pgRowSet -> {
                    List<String> cols = pgRowSet.columnsNames();
                    PgIterator iterator = pgRowSet.iterator();
                    RecordsListSelectionResult selectionResult = new RecordsListSelectionResult().setCount(pgRowSet.rowCount()).setRecords(new ArrayList<>());
                    while (iterator.hasNext()) {
                        Row row = iterator.next();
                        Map<String, Object> record = new HashMap<>();
                        for (String col : cols) {
                            record.put(col, row.getValue(col));
                        }
                        selectionResult.getRecords().add(record);
                    }
                    return selectionResult;
                });
    }

    private Tuple tupleFromArray(Object[] array) {
        Tuple tuple = Tuple.tuple();
        for (Object o : array) {
            tuple.addValue(o);
        }
        return tuple;
    }
}
