package info.xiancloud.dao.core.action.select;

import info.xiancloud.core.util.StringUtil;

/**
 * select sql action interface
 *
 * @author happyyangyuan
 */
public interface ISelect {

    /**
     * 标记查询结果为记录条数
     */
    int SELECT_COUNT = 1;
    /**
     * 设置查询结果为单记录map
     * <p>
     * 为了保证db输出格式统一, 建议一律使用'SELECT_LIST   3'
     */
    int SELECT_SINGLE = 2;
    /**
     * 标记查询结果为list
     */
    int SELECT_LIST = 3;

    /**
     * Column list you want the query to return to you.
     *
     * @return select字段列表, 字符串或字符串数组类型;特殊的,如果返回空或者空数组,那么生成 'select * '语句
     */
    Object getSelectList();

    /**
     * table name list for the select sql.
     *
     * @return table name list for the select sql, can be either array, list or string split by comma.
     */
    Object getSourceTable();

    /**
     * 返回ISelect.SELECT_COUNT/SELECT_SINGLE/SELECT_LIST
     * 查询单条/多条/count
     *
     * @return {@link #SELECT_COUNT query for the result count}, {@link #SELECT_SINGLE query for a single result}, {@link #SELECT_LIST query for a list}
     */
    int resultType();

    /**
     * 分页查询、排序查询,offset是从0开始取值!
     *
     * @param ascOrDesc     asc or desc
     * @param orderByColumn column name of order by
     * @param countPerPage  count per page
     * @param offset        offset, note that offset is started from 0
     * @return order by and page query sql tail string
     */
    static String orderByPagingQueryTail(String ascOrDesc, String orderByColumn, Integer countPerPage, Integer offset) {
        String sql = "";
        if (!StringUtil.isEmpty(orderByColumn)) {
            sql += " order by " + orderByColumn + " " + ascOrDesc;
        }
        if (countPerPage != null && countPerPage > 0 && offset != null && offset >= 0) {
            sql += " limit " + offset + " , " + countPerPage;
        }
        return sql;
    }

    static String maxQueryTail(String maxColumn) {
        return orderByPagingQueryTail("DESC", maxColumn, 1, 0);
    }

    static String minQueryTail(String minColumn) {
        return orderByPagingQueryTail("ASC", minColumn, 1, 0);
    }


}
