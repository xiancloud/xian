package info.xiancloud.plugin.dao.core.jdbc.sql;

import info.xiancloud.plugin.util.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class SelectAction extends WhereAction implements ISelect {

    protected Object executeSql(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        return ISelect.executeWhichSql(resultType(), preparedSql, sqlParams, connection);
    }

    public int resultType() {
        return SELECT_LIST;
    }

    protected String sqlHeader(Map map, Connection connection) {
        String select = "select * from ";
        if (select() != null) {
            if (select() instanceof String && !StringUtil.isEmpty(select())) {
                select = "select ".concat(select().toString()).concat(" from ");
            }
            if (select() instanceof String[]) {
                String[] array = (String[]) select();
                if (array.length > 0) {
                    String arrayStr = Arrays.toString(array);
                    select = "select ".concat(arrayStr.substring(1, arrayStr.length() - 1)).concat(" from ");
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        if (fromTable() instanceof String) {
            sb.append(fromTable());
        } else {
            for (String table : (String[]) fromTable()) {
                sb.append(getSplitter(table)).append(table);
            }
            sb = new StringBuilder(sb.substring(1));
        }
        return select.concat(sb.toString());
    }

    protected String getSplitter(String tableName) {
        if (tableName.toLowerCase().contains(" join ")) {//fixme 不支持关键字join前后不是空格而是换行的情形
            return " ";
        } else {
            return ",";
        }
    }

    public static void main(String... args) {
        System.out.print("测试测试{fjdskaj}fdsafs{},and fdasf={fdsajk>bedf}".replaceAll("\\{[^}]*\\}", "?"));
    }

}
