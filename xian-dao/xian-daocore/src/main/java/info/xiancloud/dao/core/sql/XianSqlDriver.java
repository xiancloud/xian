package info.xiancloud.dao.core.sql;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.Pair;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.insert.BatchInsertAction;

import java.util.List;
import java.util.Map;

/**
 * xian sql driver interface
 *
 * @author happyyangyuan
 */
public interface XianSqlDriver extends SqlDriverInsertion, SalDriverUpdating, SqlDriverSelection, SqlDriverDeletion {

    /**
     * produces arguments for prepared sql statement.
     * This method is reentrant.
     * This method is not suitable for batch insertion because prepared batch insertion does not use pattern sql.
     *
     * @param sqlPattern xian sql pattern
     * @param map        parameter map
     * @return parameters for the prepared sql
     */
    Object[] preparedParams(String sqlPattern, Map<String, Object> map);

    /**
     * produce a prepared sql from xian pattern sql.
     * This method is reentrant.
     * This method is not suitable for batch insertion because prepared batch insertion does not use pattern sql.
     *
     * @param xianPatternSql xian sql pattern
     * @return converted prepared sql.
     */
    String preparedSql(String xianPatternSql);

    /**
     * produces a batch prepared insertion sql and its prepared parameter array.
     * 注意:批量插入不支持本db框架的pattern模式,原因是本action的入参并不是单纯的一个map,而是map列表,这些map内的key都是一样的
     *
     * @param batchInsertAction the batch insertion action, which provides the following property for a prepared batch sql building: <br/>
     *                          1. tableName table you want to insert records into.<br/>
     *                          2. cols      table columns<br/>
     *                          3. values    values you want to insert
     * @return the prepared sql and prepared parameter array pair.
     */
    Pair<String, Object[]> preparedBatchInsertionSql(BatchInsertAction batchInsertAction);

    /**
     * list out all parameter keys in the pattern sql.
     * This method is reentrant.
     *
     * @param sqlPattern pattern sql
     * @return parameter key list
     */
    List<String> patternKeys(String sqlPattern);

    /**
     * database exception code handler method
     *
     * @param exception exception thrown by db client.
     * @param sqlAction the sql action reference
     * @return the transferred unit response
     */
    UnitResponse handleException(Throwable exception, SqlAction sqlAction);

}
