package info.xiancloud.dao.jdbc.sql;

import java.sql.SQLException;
import java.util.Map;

/**
 * 如果实现了该接口，就要打印sql日志
 *
 * @author happyyangyuan
 */
public interface ISqlLogger {

    void logSql(Map map) throws SQLException;

}
