package info.xiancloud.dao.async.postgresql;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.Pair;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.insert.BatchInsertAction;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.sqlresult.*;
import info.xiancloud.dao.core.sql.BaseSqlDriver;
import info.xiancloud.dao.core.utils.BasicSqlBuilder;
import info.xiancloud.dao.core.utils.PgPatternUtil;
import io.reactiverse.reactivex.pgclient.Tuple;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class PgSqlDriver extends BaseSqlDriver {
    private io.reactiverse.reactivex.pgclient.PgConnection pgConnection0;

    @Override
    public Single<SingleInsertionResult> insert(String patternSql, Map<String, Object> params) {
        Tuple tuple = Tuple.tuple();
        for (Object o : params.values()) {
            tuple.addValue(o);
        }
        return pgConnection0
                .rxPreparedQuery(preparedSql(patternSql), tuple)
                .flatMap(pgRowSet -> Single.just(new SingleInsertionResult()
                        .setCount(pgRowSet.rowCount())
                        //todo here is no generated id
                        .setId(null)
                ));
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
        exception.printStackTrace();
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
        return pgConnection0
                .rxPreparedQuery(preparedSql(patternSql), Tuple.of(preparedParams(patternSql, map)))
                .map(pgRowSet -> new UpdatingResult().setCount(pgRowSet.rowCount()));
    }

    @Override
    public Single<DeletionResult> delete(String patternSql, Map<String, Object> map) {
        return pgConnection0
                .rxPreparedQuery(preparedSql(patternSql), Tuple.of(preparedParams(patternSql, map)))
                .map(pgRowSet -> new DeletionResult().setCount(pgRowSet.rowCount()));
    }

    @Override
    public Single<BatchInsertionResult> batchInsert(BatchInsertAction batchInsertAction) {
        Pair<String, Object[]> pair = preparedBatchInsertionSql(batchInsertAction);
        return pgConnection0.rxPreparedQuery(pair.fst, Tuple.of(pair.snd))
                .map(pgRowSet -> new BatchInsertionResult().setCount(pgRowSet.rowCount()));
    }

    @Override
    public Single<String[]> queryCols(String tableName) {
        return pgConnection0.rxPreparedQuery("SELECT * FROM " + tableName + " WHERE 1>2 ")
                .map(pgRowSet -> {
                    List<String> cols = pgRowSet.columnsNames();
                    return cols.toArray(new String[cols.size()]);
                });
    }

    @Override
    public Single<String> getIdCol(String tableName) {
        return null;
    }

    @Override
    public Completable buildTableMetaData(Table table) {
        return null;
    }

    @Override
    public Single<RecordsListSelectionResult> select(String patternSql, Map<String, Object> map) {
        return null;
    }
}
