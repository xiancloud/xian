package info.xiancloud.dao.core.action;

import info.xiancloud.dao.core.utils.TableMetaCache;

import java.sql.*;

/**
 * single table sql action
 *
 * @author happyyangyuan
 */
public interface ISingleTableAction {
    /**
     * this method provides the table name.
     * this method is reentrant.
     *
     * @return table name
     */
    String getTableName();

    /**
     * this methods provides all column names of the specified table.
     * Read from {@link TableMetaCache}, and the cache refreshes every certain seconds.
     *
     * @return the column names of the specified table
     */
    default String[] getCols() {
        return TableMetaCache.COLS.getUnchecked(getTableName());
    }
/*
    static void doBuildTable(Table table, Connection conn) {
        String sql = "select * from `" + table.getName() + "` where 1 = 2";
        Statement stm;
        try {
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();

            JavaType javaType = new JavaType();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String colName = rsmd.getColumnName(i);
                String colClassName = rsmd.getColumnClassName(i);

                Class<?> clazz = javaType.getType(colClassName);
                if (clazz != null) {
                    table.setColumnType(colName, clazz);
                } else {
                    int type = rsmd.getColumnType(i);
                    if (type == Types.BINARY || type == Types.VARBINARY || type == Types.BLOB) {
                        table.setColumnType(colName, byte[].class);
                    } else if (type == Types.CLOB || type == Types.NCLOB) {
                        table.setColumnType(colName, String.class);
                    } else {
                        table.setColumnType(colName, String.class);
                    }
                }
            }

            rs.close();
            stm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

}
