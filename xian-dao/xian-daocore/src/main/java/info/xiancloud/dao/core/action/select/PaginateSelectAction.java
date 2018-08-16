package info.xiancloud.dao.core.action.select;

import info.xiancloud.core.Page;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.model.ddl.Column;
import info.xiancloud.dao.core.model.sqlresult.ISelectionResult;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PaginateSelectAction 分页查询Action封装
 *
 * @author hang, happyyangyuan
 */
public abstract class PaginateSelectAction extends InternalSelectionAction implements ISelect {
//
//    @Override
//    protected Single<? extends ISelectionResult> executeSql() {
//        int pageNumber = getPageNumber();
//        int pageSize = getPageSize();
//
//        String totalRowSql = replaceToCount(replaceOrderByAndLimit(getPatternSql()));
//        List<Map> result = ISelect.select();
//
//        int size = result.size();
//
//        long totalRow;
//        if (isGroupBy()) {
//            totalRow = size;
//        } else {
//            totalRow = (size > 0) ? ((Number) result.get(0).get("count(*)")).longValue() : 0;
//        }
//        if (totalRow == 0) {
//            return new Page(new ArrayList<Map>(0), pageNumber, pageSize, 0, 0);
//        }
//
//        int totalPage = (int) (totalRow / pageSize);
//        if (totalRow % pageSize != 0) {
//            totalPage++;
//        }
//
//        if (pageNumber > totalPage) {
//            return new Page(new ArrayList<Map>(0), pageNumber, pageSize, totalPage, (int) totalRow);
//        }
//        List<Map> list = ISelect.select(preparedSql, sqlParams, connection);
//        return new Page(list, pageNumber, pageSize, totalPage, (int) totalRow);
//    }
//
//    /*private static final Pattern ORDER_BY_PATTERN = Pattern.compile(
//            "order\\s+by\\s+[^,\\s]+(\\s+asc|\\s+desc)?(\\s*,\\s*[^,\\s]+(\\s+asc|\\s+desc)?)*",
//    		Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);*/
//
//    private static final Pattern COUNT_PATTERN = Pattern.compile(
//            "select.*?from",
//            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
//
//    private String replaceOrderByAndLimit(String sql) {
//        if (isOrderBy()) {
//            int index = sql.toLowerCase().lastIndexOf(InternalSelectionAction.ORDER_BY_STR);
//            return sql.substring(0, index);
//        } else {
//            int index = sql.toLowerCase().lastIndexOf(LIMIT_STR);
//            return sql.substring(0, index);
//        }
//        //return ORDER_BY_PATTERN.matcher(sql).replaceAll("");
//    }
//
//    private String replaceToCount(String sql) {
//        return COUNT_PATTERN.matcher(sql).replaceFirst("select count(*) from");
//    }
//
//    private int getPageSize() {
//        Integer pageSize = (Integer) getMap().get("pageSize");
//        if (pageSize == null) {
//            pageSize = 10;
//        }
//        return pageSize;
//    }
//
//    private int getPageNumber() {
//        Integer pageNumber = (Integer) getMap().get("pageNumber");
//        if (pageNumber == null || pageNumber < 1) {
//            pageNumber = 1;
//        }
//        return pageNumber;
//    }
//
//    private String buildOrderBy(Object orderBy) {
//        if (orderBy != null && orderBy instanceof String) {
//            String order = (String) orderBy;
//            if (!HAS_ORDER_BY_PATTERN.matcher(order).find()) {
//                return ORDER_BY_STR.concat(formatOrderBy(order));
//            }
//            return order;
//        }
//        orderBy = fmtObjectToStrArray(orderBy);
//        if (orderBy != null && orderBy instanceof String[]) {
//            String[] orderBys = (String[]) orderBy;
//            if (orderBys.length > 0) {
//                StringBuffer orderByStr = new StringBuffer(ORDER_BY_STR);
//                for (String order : orderBys) {
//                    orderByStr.append(formatOrderBy(order) + ",");
//                }
//                return orderByStr.toString().substring(0, orderByStr.toString().length() - 1);
//            }
//        }
//        return "";
//    }
//
//    private String buildGroupBy(Object groupBy) {
//        if (groupBy != null && groupBy instanceof String) {
//            String group = (String) groupBy;
//            if (!HAS_GROUP_BY_PATTERN.matcher(group).find()) {
//                return GROUP_BY_STR.concat(formatGroupBy(group));
//            }
//            return group;
//        }
//        groupBy = fmtObjectToStrArray(groupBy);
//        if (groupBy != null && groupBy instanceof String[]) {
//            String[] groupBys = (String[]) groupBy;
//            if (groupBys.length > 0) {
//                StringBuffer groupByStr = new StringBuffer(GROUP_BY_STR);
//                for (String group : groupBys) {
//                    groupByStr.append(formatGroupBy(group) + ",");
//                }
//                return groupByStr.toString().substring(0, groupByStr.toString().length() - 1);
//            }
//        }
//        return "";
//    }
//
//    protected boolean isGroupBy() {
//        return isSetGroupOrOrderBy(groupBy());
//    }
//
//    protected boolean isOrderBy() {
//        return isSetGroupOrOrderBy(orderBy());
//    }
//
//    private boolean isSetGroupOrOrderBy(Object object) {
//        if (object instanceof String) {
//            return object != null;
//        }
//        if (object instanceof String[]) {
//            return ((String[]) object).length > 0;
//        }
//        if (object instanceof Collection) {
//            return ((Collection) object).size() > 0;
//        }
//        return false;
//    }
//
//    public static String[] fmtObjectToStrArray(Object object) {
//        if (object == null) {
//            return null;
//        }
//        if (object instanceof String[]) {
//            return (String[]) object;
//        }
//        if (object instanceof Collection) {
//            Collection c = (Collection) object;
//            return (String[]) c.toArray(new String[c.size()]);
//        }
//        return null;
//    }
//
//    /**
//     * 转换一下order by字段
//     */
//    private String formatOrderBy(String order) {
//        if (!StringUtil.isEmpty(order) && getMap().get("$tableName") != null) {
//            Matcher m = CHECK_ORDER_BY_PATTERN.matcher(order);
//            if (m.find()) {
//                Column column = getColumn(m.group(1));
//                if (column != null) {
//                    return column.getName() + (m.group(2) == null ? "" : (" " + m.group(2)));
//                }
//            }
//        }
//        return StringUtil.camelToUnderline(order);
//    }
//
//    /**
//     * 转换一下group by字段
//     */
//    private String formatGroupBy(String group) {
//        if (!StringUtil.isEmpty(group) && getMap().get("$tableName") != null) {
//            Column column = getColumn(group);
//            if (column != null) {
//                if (!column.getName().equals(group)) {
//                    return StringUtil.camelToUnderline(group);
//                }
//            }
//        }
//        return StringUtil.camelToUnderline(group);
//    }
}
