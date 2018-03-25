package info.xiancloud.dao.jdbc.sql;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.global.Table;
import info.xiancloud.dao.jdbc.pool.DatasourceConfigReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public interface ISingleTableAction {
    String table();

    String[] getCols() throws SQLException;

    static String[] queryCols(String tableName, Connection connection) throws SQLException {
        List<String> resultList = new ArrayList<>();
        String sql = "SELECT column_name\n" +
                "FROM information_schema.columns\n" +
                "WHERE table_schema = DATABASE()\n" +
                "AND table_name='%s'\n";
        sql = String.format(sql, tableName);
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            resultList.add(rs.getString(1));
        }
        return resultList.toArray(new String[resultList.size()]);
    }

    //TODO 该方法应当单独抽出来放在sql语句执行工具类内
    static int dosql(String sql, Object[] st, Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < st.length; i++) {
            pstmt.setObject(i + 1, st[i]);
        }
        return pstmt.executeUpdate();
    }

    /**
     * 查询主键名
     */
    static String getIdColName(String tableName, Connection connection) {
        String alias = "idColumnName";
        Map auto_increment = (Map) (new CustomSelectAction() {
            @Override
            protected String sqlPattern(Map map, Connection connection) throws SQLException {
                return String.format("SELECT k.COLUMN_NAME as %s\n" +
                        "FROM information_schema.table_constraints t\n" +
                        "LEFT JOIN information_schema.key_column_usage k\n" +
                        "USING(constraint_name,table_schema,table_name)\n" +
                        "WHERE t.constraint_type='PRIMARY KEY'\n" +
                        "    AND t.table_schema=DATABASE() \n" +
                        "    AND t.table_name='%s';", alias, tableName);
            }

            @Override
            public int resultType() {
                return SELECT_SINGLE;
            }
        }.execute(null, null, connection).getData());
        return auto_increment.get(StringUtil.underlineToCamel(alias)).toString();
    }

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
    }

    static void main(String... args) {
        try (
                Connection conn = DriverManager.getConnection(DatasourceConfigReader.getReadUrl(), DatasourceConfigReader.getReadUser(), DatasourceConfigReader.getReadPwd())
        ) {
            String col = getIdColName("ucs_customer", conn);
            System.out.println(col);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
