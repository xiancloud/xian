package info.xiancloud.dao.core.utils;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Pair;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.StringUtil;

import java.util.*;

/**
 * 基本的增删改查sql拼装器，对单表的增删改支持得特别好;
 * 缺点是要求入参必须是map
 *
 * @author happyyangyuan
 */
public class BasicSqlBuilder {

    public static String buildUpdateSqlWithValue(String tableName, String[] columns, Map msgMap, String... whereCols) {
        return SqlUtils.mapToSql(buildUpdateSql(tableName, columns, msgMap, whereCols), msgMap);
    }

    public static String buildInsertSqlWithValue(String tableName, String[] cols, Map data) {
        return SqlUtils.mapToSql(insertSQL(tableName, cols, data), data);
    }

    public static String buildDeleteSqlWithValue(String tableName, String[] cols, Map msgMap, String... whereCols) {
        Map whereData = splitMap(msgMap, whereCols)[1];
        return SqlUtils.mapToSql(deleteSQL(tableName, cols, whereData), msgMap);
    }

    public static String buildBasicQuerySqlWithValue(String selectFrom, Map msgMap, String[] cols, String... whereCols) {
        Map whereData = splitMap(msgMap, whereCols)[1];
        return SqlUtils.mapToSql(selectSQL(selectFrom, whereData, cols), msgMap);
    }

    public static String buildUpdateSql(String tableName, String[] columns, Map msgMap, String... whereCols) {
        Map[] maps = splitMap(msgMap, whereCols);
        return buildUpdateSql(tableName, columns, maps[0], maps[1]);
    }

    public static String buildUpdateSql(String tableName, String[] columns, Map dataMap, Map whereMap) {
        return updateSQL(tableName, columns, dataMap, whereMap);
    }

    public static String buildInserSql(String tableName, String[] cols, Map data) {
        return insertSQL(tableName, cols, data);
    }

    public static String buildDeleteSql(String tableName, String[] cols, Map whereData) {
        return deleteSQL(tableName, cols, whereData);
    }

    public static String buildBasicQuerySql(String selectFrom, Map whereData, String[] cols) {
        return selectSQL(selectFrom, whereData, cols);
    }

    private static Map<String, Object>[] splitMap(Map<String, Object> map, String... whereCols) {
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Object> whereMap = new HashMap<>();
        dataMap.putAll(map);
        for (String col : whereCols) {
            dataMap.remove(col);
        }
        for (String col : whereCols) {
            whereMap.put(col, map.get(col));
        }
        return new Map[]{dataMap, whereMap};
    }


    private static String selectSQL(String sqlHeader, Map whereData, String[] cols) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append(sqlHeader);
        StringBuilder whereSb = new StringBuilder();
        List<Object> da = new ArrayList<>();


        for (int i = 0; i < cols.length; i++) {
            Object ob = whereData.get(cols[i]);
            if (ob != null) {
                da.add(ob);
                if (i == cols.length - 1) {
                    whereSb.append(cols[i]).append("=".concat(getCurlyBracedCamalPattern(cols[i])));
                } else {
                    whereSb.append(cols[i] + "=".concat(getCurlyBracedCamalPattern(cols[i])).concat(" and "));
                }
            }

        }//end for
        sqlSb.append("");
        sqlSb.append(" where ");
        sqlSb.append(whereSb.toString());
        String sql = sqlSb.toString().trim();
        if (sql.matches("^.*and$")) {
            sql = sql.substring(0, sql.length() - 3);
        }
        return sql;
    }

    private static String insertSQL(String tableName, String[] cols, Map data) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("insert into ").append(tableName).append("( ");
        StringBuilder colParamB = new StringBuilder();
        StringBuilder qmSb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            Object ob = data.get(StringUtil.underlineToCamel(cols[i]));
            if (ob != null) {
                colParamB.append(cols[i]).append(",");
                qmSb.append(getCurlyBracedCamalPattern(cols[i]).concat(","));
            }
        }
        sqlSb.append(colParamB.toString());
        sqlSb.append(") value (");
        sqlSb.append(qmSb.toString());
        sqlSb.append(")");

        String sql = sqlSb.toString();
        sql = sql.replace(",)", ")");
        return sql;
    }

    /**
     * build the batch insertion prepared sql for jdbc standard.
     * Jdbc standard prepared sql is with "?" to represents the arguments, eg.<p>
     * <code>INSERT INTO TABLE0 VALUES (?,?,?),(?,?,?)</code>
     * </p>
     *
     * @param tableName table name
     * @param cols      table columns array
     * @param dataList  values for the batch insertion sql
     * @return A pair with prepared sql as the first element and the prepared parameter array as the second.
     */
    public static Pair<String, Object[]> buildJdbcBatchInsertPreparedSQL(String tableName, String[] cols, List<Map/*<String, Object>*/> dataList) {
        Set<String> validCols = findValidCols(cols, dataList);
        List<Object> valuesArray = new ArrayList<>();
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("insert into ").append(tableName).append("(");
        StringBuilder colParamB = new StringBuilder();
        StringBuilder qmSb = new StringBuilder();
        Iterator<String> it = validCols.iterator();
        while (it.hasNext()) {
            String col = it.next();
            if (!it.hasNext()) {
                colParamB.append(col);
            } else {
                colParamB.append(col).append(",");
            }
        }
        sqlSb.append(colParamB.toString());
        sqlSb.append(") values ");

        for (Map map : dataList) {
            qmSb.append("(");
            for (String col : validCols) {
                Object value = map.get(StringUtil.underlineToCamel(col));
                valuesArray.add(toDatabaseSupportedValue(value));
                qmSb.append("?,");
            }
            deleteLastCommaIfNecessary(qmSb);
            qmSb.append("),");
        }
        deleteLastCommaIfNecessary(qmSb);
        sqlSb.append(qmSb.toString());
        String sql = sqlSb.toString();
        return new Pair<>(sql, valuesArray.toArray());
    }

    /**
     * Build the batch insertion prepared sql for reactive postgresql standard.
     * Jdbc standard prepared sql is with "$i" to represents the arguments. "i" starts with 1. eg.
     * <p>
     * <code>INSERT INTO TABLE0 VALUES ($1,$2,$3),($4,$5,$6)</code>
     * </p>
     *
     * @param tableName table name
     * @param cols      table columns array
     * @param dataList  values for the batch insertion sql
     * @return A pair with prepared sql as the first element and the prepared parameter array as the second.
     */
    public static Pair<String, Object[]> buildPgBatchInsertPreparedSQL(String tableName, String[] cols, List<Map/*<String, Object>*/> dataList) {
        Set<String> validCols = findValidCols(cols, dataList);
        List<Object> valuesArray = new ArrayList<>();
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("INSERT INTO ").append(tableName).append("(");
        StringBuilder colParamB = new StringBuilder();
        StringBuilder qmSb = new StringBuilder();
        Iterator<String> it = validCols.iterator();
        while (it.hasNext()) {
            String col = it.next();
            if (!it.hasNext()) {
                colParamB.append(col);
            } else {
                colParamB.append(col).append(",");
            }
        }
        sqlSb.append(colParamB.toString());
        sqlSb.append(") VALUES ");

        //pg arg index starts with 1
        int i = 1;
        for (Map<String, Object> map : dataList) {
            qmSb.append("(");
            for (String col : validCols) {
                Object value = map.get(StringUtil.underlineToCamel(col));
                valuesArray.add(toDatabaseSupportedValue(value));
                qmSb.append("$").append(i).append(",");
                i++;
            }
            deleteLastCommaIfNecessary(qmSb);
            qmSb.append("),");
        }
        deleteLastCommaIfNecessary(qmSb);
        sqlSb.append(qmSb.toString());
        return new Pair<>(sqlSb.toString(), valuesArray.toArray());
    }

    /**
     * Convert Java value to database supported value.
     * Please refer to "https://www.cnblogs.com/jerrylz/p/5814460.html" for detail.
     *
     * @param value java value, can be any type.
     * @return database supported value
     */
    private static Object toDatabaseSupportedValue(Object value) {
        if (value instanceof java.lang.String ||
                value instanceof byte[] ||
                value instanceof Byte[] ||
                value instanceof java.lang.Long ||
                value instanceof java.lang.Integer ||
                value instanceof java.lang.Boolean ||
                value instanceof java.math.BigInteger ||
                value instanceof java.lang.Float ||
                value instanceof java.lang.Double ||
                value instanceof java.math.BigDecimal ||
                value instanceof java.sql.Date ||
                value instanceof java.util.Date ||
                /*
                because java.sql.Time and java.sql.Timestamp is subclass of java.sql.Date so the checks always return true
                value instanceof java.sql.Time || value instanceof java.sql.Timestamp ||
                */
                value instanceof java.util.Calendar
                ) {
            return value;
        } else {
            return Reflection.toType(value, String.class);
        }
    }


    private static void deleteLastCommaIfNecessary(StringBuilder qmSb) {
        if (qmSb.length() > 0 && qmSb.charAt(qmSb.length() - 1) == ',') {
            LOG.debug("这里是删除最后一个逗号");
            LOG.debug("防止空列表插入时，没有最有一个逗号的问题。");
            qmSb.deleteCharAt(qmSb.length() - 1);
        }
    }

    private static Set<String> findValidCols(String[] allCols, List<Map/*<String, Object>*/> dataList) {
        LOG.debug("本方法为满足批量插入的灵活性,搜索出所有的有效列名,允许map列表内的key参差不齐的场景");
        Set<String> validCols = new HashSet<>();
        for (String col : allCols) {
            String key = StringUtil.underlineToCamel(col);
            for (Map map : dataList) {
                if (map.containsKey(key)) {
                    validCols.add(col);
                }
            }
        }
        return validCols;
    }

    private static String getCurlyBracedCamalPattern(String key) {
        return "{".concat(StringUtil.underlineToCamel(key)).concat("}");
    }

    private static String updateSQL(String tableName, String[] cols, Map data, Map whereData) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("update ").append(tableName).append(" set ");
        StringBuilder dataSb = new StringBuilder();
        StringBuilder whereSb = new StringBuilder();
        boolean startSet = true;
        for (int i = 0; i < cols.length; i++) {
            Object ob = data.get(cols[i]);
            if (ob == null) {
                continue;
            }
            if (startSet) {
                dataSb.append(cols[i]).append("=".concat(getCurlyBracedCamalPattern(cols[i])));
                startSet = false;
            } else {
                dataSb.append(",").append(cols[i]).append("=".concat(getCurlyBracedCamalPattern(cols[i])));
            }
        }//end for

        for (int i = 0; i < cols.length; i++) {
            Object ob = whereData.get(cols[i]);
            if (ob != null) {
                if (i == cols.length - 1) {
                    whereSb.append(cols[i]).append("=".concat(getCurlyBracedCamalPattern(cols[i])));
                } else {
                    whereSb.append(cols[i]).append("=".concat(getCurlyBracedCamalPattern(cols[i])).concat(" and "));
                }
            }

        }//end for
        sqlSb.append(dataSb.toString());
        sqlSb.append(" where ");
        sqlSb.append(whereSb.toString());
        String sql = sqlSb.toString().trim();
        //去除最后的and
        if (sql.matches("^.*and$")) {
            sql = sql.substring(0, sql.length() - 3);
        }
        return sql;
    }

    private static String deleteSQL(String tableName, String[] cols, Map whereData) {
        StringBuffer sqlSb = new StringBuffer();
        sqlSb.append("delete from " + tableName + " where ");
        StringBuffer whereSb = new StringBuffer();
        List<Object> da = new ArrayList<>();


        for (int i = 0; i < cols.length; i++) {
            Object ob = whereData.get(cols[i]);
            if (ob != null) {
                da.add(ob);
                if (i == cols.length - 1) {
                    whereSb.append(cols[i]).append("=".concat(getCurlyBracedCamalPattern(cols[i])));
                } else {
                    whereSb.append(cols[i]).append("=".concat(getCurlyBracedCamalPattern(cols[i])).concat(" and "));
                }
            }

        }//end for
        sqlSb.append(whereSb.toString());
        String sql = sqlSb.toString().trim();
        if (sql.matches("^.*and$")) {
            sql = sql.substring(0, sql.length() - 3);
        }
        return sql;
    }

}
