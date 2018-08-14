package info.xiancloud.dao.core.action;

import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.sql.XianSqlDriver;
import info.xiancloud.dao.core.units.DaoUnit;
import io.reactivex.Single;

import java.util.Map;

/**
 * interface of sql action
 *
 * @author happyyangyuan
 */
public interface SqlAction extends ISqlLogger {

    /**
     * execution of this sql action
     *
     * @param daoUnit    dao unit object
     * @param map        unit arguments
     * @param connection represents a database connection
     * @return a single unit response object
     */
    Single<UnitResponse> execute(Unit daoUnit, Map<String, Object> map, XianConnection connection);

    /**
     * @return sql driver
     */
    XianSqlDriver getSqlDriver();

    /**
     * get the current pattern sql.
     * lazy-init
     *
     * @return the current pattern sql.
     */
    String getPatternSql();

    /**
     * Safety check.
     * Overwrite this method to provide your own check logic.
     *
     * @return checking result, succeeded response or failure response, no exception should be thrown.
     */
    UnitResponse check();

    /**
     * argument map of this sql action.
     *
     * @return argument map
     */
    Map<String, Object> getMap();

    /**
     * @return connection
     */
    XianConnection getConnection();

    /**
     * @return * dao unit
     */
    DaoUnit getDaoUnit();

    /**
     * get the fully assembled sql.
     * <p>
     * Note that this sql is not the really sql sent to database. This fully assembled sql is usually for log and for human reading.
     * We only send prepared sql to the database for execution.
     * For performance consideration, we may cut the full sql if it too long for assembling.
     *
     * @return assembled full sql
     */
    String getFullSql();

}
