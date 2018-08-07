package info.xiancloud.dao.jdbc.sql;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
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
     * @return select字段列表, 字符串或字符串数组类型;特殊的,如果返回空或者空数组,那么生成 'select * '语句
     */
    Object select();

    Object fromTable();

    /**
     * 返回ISelect.SELECT_COUNT/SELECT_SINGLE/SELECT_LIST
     * 查询单条/多条/count
     */
    int resultType();


    static Object executeWhichSql(int resultType, String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        if (resultType == ISelect.SELECT_LIST) {
            return ISelect.select(preparedSql, sqlParams, connection);
        }
        if (resultType == ISelect.SELECT_COUNT) {
            return ISelect.selectCount(preparedSql, sqlParams, connection);
        }
        if (resultType == ISelect.SELECT_SINGLE) {
            return ISelect.selectSingleResult(preparedSql, sqlParams, connection);
        }
        throw new RuntimeException("不支持的查询类型:" + resultType);
    }

    /**
     * 工具方法
     */
    static List<Map> select(String sql, Object[] objectArr, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < objectArr.length; i++) {
            ps.setObject(i + 1, objectArr[i]);
        }
        ResultSet rs = ps.executeQuery();
        List list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount(); //Map rowData;
        while (rs.next()) { //rowData = new HashMap(columnCount);
            Map<String, Object> rowData = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                /*注意两点:多表关联查询会出现字段名重复的情形,重复字段的colName会自动被替换成tableName.colName; 使用getColumnLabel(i)而不使用getColumnName(i)来支持sql中的别名*/
                String colName = StringUtil.underlineToCamel(md.getColumnLabel(i));
                colName = rowData.containsKey(colName) ? md.getTableName(i).concat(".").concat(colName) : colName;
                rowData.put(colName, rs.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }

    static Long selectCount(String sql, Object[] objectArr, Connection conn) throws SQLException {
        List<Map> count = select(sql, objectArr, conn);
        return (Long) count.get(0).values().iterator().next();
    }

    static Map selectSingleResult(String sql, Object[] objectArr, Connection conn) throws SQLException {
        List<Map> list = select(sql, objectArr, conn);
        if (list == null || list.isEmpty()) {
            LOG.warn("查询结果为空,返回null...");
            return null;
        }
        return list.get(0);
    }

    /**
     * 分页查询、排序查询,offset是从0开始取值!
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
