package info.xiancloud.dao.core.action;

import java.util.Map;

/**
 * Interface for sql logging
 *
 * @author happyyangyuan
 */
public interface ISqlLogger {

    /**
     * print sql
     *
     * @param map dao unit arguments
     */
    void logSql(Map<String, Object> map);

}
