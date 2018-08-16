package info.xiancloud.dao.core.action.select;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.model.ddl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Abstract internal selection action.
 * With help of this internal selection action, you can submit a query request without writing any sql clause.
 * This action can read operators in parameter map and construct a sql all by itself. You don't need to write any sql statement.
 *
 * @author happyyangyuan
 */
public abstract class InternalSelectionAction extends CustomSelectWhereAction implements ISelect {

//    final protected static String LIMIT_STR = " limit ";
//
//    /**
//     * @return 应当返回完整的order by子句,或者穿空忽略order by
//     */
//    protected Object orderBy() {
//        return getMap().get("$orderBy");
//    }
//
//    /**
//     * @return 应当返回完整的group by子句,或者穿空忽略group by
//     */
//    protected Object groupBy() {
//        return getMap().get("$groupBy");
//    }
//
//    /**
//     * 一堆正则匹配
//     */
//    final protected static Pattern HAS_GROUP_BY_PATTERN = Pattern.compile(
//            "group\\s+by*",
//            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
//    final protected static Pattern HAS_ORDER_BY_PATTERN = Pattern.compile(
//            "order\\s+by*",
//            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
//    final protected static Pattern CHECK_ORDER_BY_PATTERN = Pattern.compile(
//            "^(.+?)\\s*(asc|desc){0,}$",
//            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
//    final protected static String ORDER_BY_STR = "order by ";
//    final protected static String GROUP_BY_STR = "group by ";
//
//    @Override
//    protected String patternSql() {
//        return buildSqlPattern();
//    }
//
//    /**
//     * Rebuild the pattern sql
//     */
//    private String buildSqlPattern() {
//        List<Object> preparedParams = new ArrayList<>();
//        StringBuilder sb = new StringBuilder();
//
//        for (Entry<String, Object> entry : getMap().entrySet()) {
//            Column column = getColumn(entry.getKey());
//            if (column != null) {
//                SearchCondition searchCondition = Operator.rule(column, entry.getValue());
//                Operator.buildSQL(sb, searchCondition.getOperator(), column.getName(), searchCondition.getValue(), "", preparedParams);
//            }
//        }
//        this.preparedSql = sqlHeader().concat(" where 1>0 ").concat(sb.toString()).concat(" ").concat(sqlTail());
//        this.sqlParams = preparedParams.toArray();
//        return preparedSql;
//    }
//
//    /**
//     * 检查字段是否属于表字段,传入值为驼峰值，表字段为下划线值
//     */
//    protected Column getColumn(String column) {
//        if (StringUtil.isEmpty(column) || column.startsWith("$")) {
//            return null;
//        }
//        if (getSourceTable() instanceof String) {
//            String tableName = (String) getSourceTable();
//            if (!StringUtil.isEmpty(tableName)) {
//                Table table = TableHeader.getTable(tableName);
//                if (table != null) {
//                    column = column.trim();
//                    Map<String, Class<?>> columns = TableHeader.getTableColumnTypeMap(table.getName());
//                    if (columns == null) {
//                        throw new RuntimeException("table %a not ");
//                    }
//                    for (Map.Entry<String, Class<?>> entry : columns.entrySet()) {
//                        String key = entry.getKey();
//                        if (column.equals(key)) {
//                            return Column.create(key, entry.getValue());
//                        }
//                        if (column.equals(StringUtil.underlineToCamel(key))) {
//                            return Column.create(key, entry.getValue());
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    private String sqlTail() {
//        return buildLimit(sqlTail0());
//    }
//
//    private String buildLimit(String select) {
//        int offset = getPageSize() * (getPageNumber() - 1);
//        // limit can use one or two '?' to pass paras
//        return select + LIMIT_STR + offset + ", " + getPageSize();
//    }
//
//    private String sqlTail0() {
//        String groupByClause = isGroupBy() ? " ".concat(buildGroupBy(groupBy())).concat(" ") : "";
//        String orderByClause = isOrderBy() ? " ".concat(buildOrderBy(orderBy())).concat(" ") : "";
//        return groupByClause.concat(orderByClause);
//    }
}
