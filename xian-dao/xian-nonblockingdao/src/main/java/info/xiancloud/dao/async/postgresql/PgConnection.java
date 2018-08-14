package info.xiancloud.dao.async.postgresql;

import info.xiancloud.dao.core.connection.BaseXianConnection;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.model.sqlresult.SingleInsertionResult;
import info.xiancloud.dao.core.utils.PatternUtil;
import io.reactiverse.reactivex.pgclient.Tuple;
import io.reactivex.Single;

import java.util.Map;

/**
 * Async non-transactional postgreSQL connection implementation for {@link XianConnection}
 *
 * @author happyyangyuan
 */
public class PgConnection extends BaseXianConnection {

    /**
     * postgresql rxJava2 style connection reference.
     */
    private io.reactiverse.reactivex.pgclient.PgConnection pgConnection0;

    public PgConnection setPgConnection0(io.reactiverse.reactivex.pgclient.PgConnection pgConnection0) {
        this.pgConnection0 = pgConnection0;
        pgConnection0.closeHandler(event -> closed = true);
        return this;
    }

    public io.reactiverse.reactivex.pgclient.PgConnection getPgConnection0() {
        return pgConnection0;
    }

    @Override
    public Single<SingleInsertionResult> insert(String sql) {
        return pgConnection0
                .rxQuery(sql)
                .flatMap(pgRowSet -> Single.just(new SingleInsertionResult()
                                .setCount(pgRowSet.rowCount())
                                //todo generated id?
                                .setId(pgRowSet.size())
                        )
                );
    }

    @Override
    public Single<SingleInsertionResult> insert(String sqlPattern, Map<String, Object> params) {
        //todo postgresql prepared sql
        String preparedSql = PatternUtil.getPreparedSql(sqlPattern);
        Tuple tuple = Tuple.tuple();
        for (Object o : params.values()) {
            tuple.addValue(o);
        }
        return pgConnection0
                .rxPreparedQuery(preparedSql, tuple)
                .flatMap(pgRowSet -> Single.just(new SingleInsertionResult()
                        .setCount(pgRowSet.rowCount())
                        //todo generated id?
                        .setId(pgRowSet.size())
                ));
    }

}
