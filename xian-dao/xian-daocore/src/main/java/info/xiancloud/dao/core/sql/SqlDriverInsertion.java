package info.xiancloud.dao.core.sql;

import info.xiancloud.dao.core.action.insert.BatchInsertAction;
import info.xiancloud.dao.core.model.sqlresult.BatchInsertionResult;
import info.xiancloud.dao.core.model.sqlresult.SingleInsertionResult;
import io.reactivex.Single;

import java.util.Map;

/**
 * Ability for xian connection.
 * interface for insertion operation
 *
 * @author happyyangyuan
 */
public interface SqlDriverInsertion extends ISingleTableSqlDriver {

    /**
     * execute the given insertion sql
     *
     * @param patternSql the xian-style sql pattern string. eg. " insert into {table_name} value ({param01},{param02},2,true)"
     * @param value      the key value parameters
     * @return The rxJava2 single insertion result.
     */
    Single<SingleInsertionResult> insert(String patternSql, Map<String, Object> value);

    /**
     * execute the given batch insertion sql
     *
     * @param batchInsertAction the batch insertion action
     * @return deferred batch insertion result.
     */
    Single<BatchInsertionResult> batchInsert(BatchInsertAction batchInsertAction);


}
