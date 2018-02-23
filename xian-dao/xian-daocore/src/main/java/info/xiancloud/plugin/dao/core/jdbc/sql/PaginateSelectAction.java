package info.xiancloud.plugin.dao.core.jdbc.sql;

import info.xiancloud.plugin.Page;
import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * PaginateSelectAction 分页查询Action封装
 *
 * @author hang
 */
public abstract class PaginateSelectAction extends QueryAction {
    final protected String sqlTail(DaoUnit daoUnit, Map map, Connection connection) {
        return buildLimit(super.sqlTail(daoUnit, map, connection));
    }

    protected Object executeSql(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        int pageNumber = getPageNumber();
        int pageSize = getPageSize();

        String totalRowSql = replaceToCount(replaceOrderByAndLimit(preparedSql));
        List<Map> result = ISelect.select(totalRowSql, sqlParams, connection);

        int size = result.size();

        long totalRow;
        if (isGroupBy()) {
            totalRow = size;
        } else {
            totalRow = (size > 0) ? ((Number) result.get(0).get("count(*)")).longValue() : 0;
        }
        if (totalRow == 0) {
            return new Page(new ArrayList<Map>(0), pageNumber, pageSize, 0, 0);
        }

        int totalPage = (int) (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }

        if (pageNumber > totalPage) {
            return new Page(new ArrayList<Map>(0), pageNumber, pageSize, totalPage, (int) totalRow);
        }
        List<Map> list = ISelect.select(preparedSql, sqlParams, connection);
        return new Page(list, pageNumber, pageSize, totalPage, (int) totalRow);
    }

    /*private static final Pattern ORDER_BY_PATTERN = Pattern.compile(
            "order\\s+by\\s+[^,\\s]+(\\s+asc|\\s+desc)?(\\s*,\\s*[^,\\s]+(\\s+asc|\\s+desc)?)*",
    		Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);*/
    private static final Pattern COUNT_PATTERN = Pattern.compile(
            "select.*?from",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    final protected static String limit_str = " limit ";

    private String replaceOrderByAndLimit(String sql) {
        if (isOrderBy()) {
            int index = sql.toLowerCase().lastIndexOf(order_by_str);
            return sql.substring(0, index);
        } else {
            int index = sql.toLowerCase().lastIndexOf(limit_str);
            return sql.substring(0, index);
        }
        //return ORDER_BY_PATTERN.matcher(sql).replaceAll("");
    }

    private String replaceToCount(String sql) {
        return COUNT_PATTERN.matcher(sql).replaceFirst("select count(*) from");
    }

    private String buildLimit(String select) {
        int offset = getPageSize() * (getPageNumber() - 1);
        StringBuilder ret = new StringBuilder(select);
        ret.append(limit_str).append(offset).append(", ").append(getPageSize());    // limit can use one or two '?' to pass paras
        return ret.toString();
    }

    private int getPageSize() {
        Integer pageSize = (Integer) map.get("pageSize");
        if (pageSize == null) {
            pageSize = 10;
        }
        return pageSize;
    }

    private int getPageNumber() {
        Integer pageNumber = (Integer) map.get("pageNumber");
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        return pageNumber;
    }
}
