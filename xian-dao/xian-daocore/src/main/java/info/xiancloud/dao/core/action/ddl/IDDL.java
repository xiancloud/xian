package info.xiancloud.dao.core.action.ddl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DDL is
 * Data Definition Language.
 * eg.
 * <code>
 * CREATE
 * ALTER
 * DROP
 * TRUNCATE
 * COMMENT
 * RENAME
 * </code>
 * DDL needs no commitment.
 *
 * @author happyyangyuan
 * @deprecated Not used by now.
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
