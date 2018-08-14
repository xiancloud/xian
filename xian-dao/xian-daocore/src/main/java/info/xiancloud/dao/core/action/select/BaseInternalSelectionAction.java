package info.xiancloud.dao.core.action.select;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.action.AbstractSqlAction;
import info.xiancloud.dao.core.model.ddl.Column;
import info.xiancloud.dao.core.model.ddl.Para;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.ddl.TableHeader;
import info.xiancloud.dao.core.model.sqlresult.ISelectionResult;
import info.xiancloud.dao.jdbc.SqlUtils;
import io.reactivex.Single;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * abstract internal selection action.
 * With help of this internal selection action, you can submit a query request without writing any sql clause.
 * This action can read operators in parameter map and construct a sql all by itself. You don't need to write any sql statement.
 */
abstract class BaseInternalSelectionAction extends AbstractSqlAction /*SelectAction*/ /*WhereAction*/ implements ISelect {

    @Override
    protected final Single<? extends ISelectionResult> executeSql() {
        return SelectActionHelper.executeWhichSql(this, resultType());
    }

    @Override
    public int resultType() {
        return SELECT_LIST;
    }
    /*
    @Override
    protected String sqlHeader() {
        return buildSelect();
    }

    private String buildSelect() {
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
    }*/

    @Override
    protected String sqlPattern() {
        return buildSqlPattern();
    }

    @Override
    public final Object getSelectList() {
        return null;
    }

    @Override
    public Object getSourceTable() {
        Object tableName = getMap().get("$tableName");
        if (tableName instanceof String) {
            return TableHeader.getTableName(getMap().get("$tableName").toString());
        }
        return tableName;
    }

    /*
    @Override
    protected String[] where() {
        return new String[]{};
    }*/

    /**
     * @return 应当返回完整的order by子句,或者穿空忽略order by
     */
    protected Object orderBy() {
        return getMap().get("$orderBy");
    }

    /**
     * @return 应当返回完整的group by子句,或者穿空忽略group by
     */
    protected Object groupBy() {
        return getMap().get("$groupBy");
    }

    /**
     * 一堆正则匹配
     */
    final protected static Pattern HAS_GROUP_BY_PATTERN = Pattern.compile(
            "group\\s+by*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    final protected static Pattern HAS_ORDER_BY_PATTERN = Pattern.compile(
            "order\\s+by*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    final protected static Pattern CHECK_ORDER_BY_PATTERN = Pattern.compile(
            "^(.+?)\\s*(asc|desc){0,}$",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    final protected static String ORDER_BY_STR = "order by ";
    final protected static String GROUP_BY_STR = "group by ";

    /**
     * rebuild the pattern sql
     */
    private String buildSqlPattern() {
        if (where() == null || where().length == 0) {
            ArrayList<Object> paramArrayList = new ArrayList<Object>();
            StringBuilder sb = new StringBuilder();

            Map<String, Object> argMap = map;
            for (Entry<String, Object> entry : argMap.entrySet()) {
                Column column = getColumn(entry.getKey());
                if (column != null) {
                    Para cir = Cnd.rule(column, entry.getValue());
                    Cnd.buildSQL(sb, cir.getType(), column.getName(), cir.getValue(), "", paramArrayList);
                }
            }
            this.preparedSql = sqlHeader(map, connection).concat(" where 1>0 ").concat(sb.toString()).concat(" ").concat(sqlTail(daoUnit, map, connection));
            this.sqlParams = paramArrayList.toArray();
            return this.preparedSql;
        }
        return SqlUtils.mapToSql(adjustInClause(super.patternSql(map, connection)), map);
    }

    /**
     * 构建order by语句
     */
    private String buildOrderBy(Object orderBy) {
        if (orderBy != null && orderBy instanceof String) {
            String order = (String) orderBy;
            if (!HAS_ORDER_BY_PATTERN.matcher(order).find()) {
                return ORDER_BY_STR.concat(formatOrderBy(order));
            }
            return order;
        }
        orderBy = fmtObjectToStrArray(orderBy);
        if (orderBy != null && orderBy instanceof String[]) {
            String[] orderBys = (String[]) orderBy;
            if (orderBys.length > 0) {
                StringBuffer orderByStr = new StringBuffer(ORDER_BY_STR);
                for (String order : orderBys) {
                    orderByStr.append(formatOrderBy(order) + ",");
                }
                return orderByStr.toString().substring(0, orderByStr.toString().length() - 1);
            }
        }
        return "";
    }

    /**
     * 构建group by语句
     */
    private String buildGroupBy(Object groupBy) {
        if (groupBy != null && groupBy instanceof String) {
            String group = (String) groupBy;
            if (!HAS_GROUP_BY_PATTERN.matcher(group).find()) {
                return GROUP_BY_STR.concat(formatGroupBy(group));
            }
            return group;
        }
        groupBy = fmtObjectToStrArray(groupBy);
        if (groupBy != null && groupBy instanceof String[]) {
            String[] groupBys = (String[]) groupBy;
            if (groupBys.length > 0) {
                StringBuffer groupByStr = new StringBuffer(GROUP_BY_STR);
                for (String group : groupBys) {
                    groupByStr.append(formatGroupBy(group) + ",");
                }
                return groupByStr.toString().substring(0, groupByStr.toString().length() - 1);
            }
        }
        return "";
    }

    protected boolean isGroupBy() {
        return isSetGroupOrOrderBy(groupBy());
    }

    protected boolean isOrderBy() {
        return isSetGroupOrOrderBy(orderBy());
    }

    private boolean isSetGroupOrOrderBy(Object object) {
        if (object instanceof String) {
            return object != null;
        }
        if (object instanceof String[]) {
            return ((String[]) object).length > 0;
        }
        if (object instanceof Collection) {
            return ((Collection) object).size() > 0;
        }
        return false;
    }

    public static String[] fmtObjectToStrArray(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String[]) {
            return (String[]) object;
        }
        if (object instanceof Collection) {
            Collection c = (Collection) object;
            return (String[]) c.toArray(new String[c.size()]);
        }
        return null;
    }

    /**
     * 检查字段是否属于表字段,传入值为驼峰值，表字段为下划线值
     */
    protected Column getColumn(String column) {
        if (StringUtil.isEmpty(column) || column.startsWith("$")) {
            return null;
        }
        if (fromTable() instanceof String) {
            String tableName = (String) fromTable();
            if (!StringUtil.isEmpty(tableName)) {
                Table table = TableHeader.getTable(tableName);
                if (table != null) {
                    column = column.trim();
                    Map<String, Class<?>> columns = TableHeader.getTableColumnTypeMap(table.getName(), connection);
                    if (columns == null) {
                        throw new RuntimeException("table %a not ");
                    }
                    for (Entry<String, Class<?>> entry : columns.entrySet()) {
                        String key = entry.getKey();
                        if (column.equals(key)) {
                            return Column.create(key, entry.getValue());
                        }
                        if (column.equals(StringUtil.underlineToCamel(key))) {
                            return Column.create(key, entry.getValue());
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 转换一下order by字段
     */
    private String formatOrderBy(String order) {
        if (!StringUtil.isEmpty(order) && map.get("$tableName") != null) {
            Matcher m = CHECK_ORDER_BY_PATTERN.matcher(order);
            if (m.find()) {
                Column column = getColumn(m.group(1));
                if (column != null) {
                    return column.getName() + (m.group(2) == null ? "" : (" " + m.group(2)));
                }
            }
        }
        return StringUtil.camelToUnderline(order);
    }

    /**
     * 转换一下group by字段
     */
    private String formatGroupBy(String group) {
        if (!StringUtil.isEmpty(group) && map.get("$tableName") != null) {
            Column column = getColumn(group);
            if (column != null) {
                if (!column.getName().equals(group)) {
                    return StringUtil.camelToUnderline(group);
                }
            }
        }
        return StringUtil.camelToUnderline(group);
    }
}
