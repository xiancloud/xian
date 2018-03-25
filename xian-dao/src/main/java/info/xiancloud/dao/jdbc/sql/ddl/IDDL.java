package info.xiancloud.dao.jdbc.sql.ddl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 继承了此接口的action都是DDL语句的action
 *
 * @author happyyangyuan
 */
public interface IDDL {
    /**
     * 执行DDL语句
     */
    static int executeDDL(String preparedSql, Object[] params, Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(preparedSql);
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        return pstmt.executeUpdate();
    }
}
