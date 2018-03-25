package info.xiancloud.dao.jdbc;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.utils.date.DateConverterFactory;
import info.xiancloud.dao.utils.string.MapFormat;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * sql工具类
 */
public class SqlUtils {

    /**
     * mybatis自带的执行sql文件的工具
     *
     * @param massSql     要执行的一批脚本
     * @param stopOnError 遇到错误时是否停止
     * @return 执行结果
     * @deprecated 暂时不想引入mybatis，不支持此操作
     */
    public static String executeMassSqlUsingMybatis(String massSql, boolean stopOnError) throws SQLException {
        /*try (Connection con = PoolFactory.getPool().getWriteConnection()) {
            ScriptRunner sr = new ScriptRunner(con);
            sr.setStopOnError(stopOnError);
            sr.setAutoCommit(true);
            StringWriter stringWriter = new StringWriter();
            PrintWriter logWriter = new PrintWriter(stringWriter);
            sr.setLogWriter(logWriter);
            sr.setErrorLogWriter(logWriter);
            sr.setSendFullScript(false);//注意!这里如果设置为全sql发送,那么多段sql执行就会报错!所以必须设置为false
            sr.runScript(new StringReader(massSql));
            return stringWriter.toString();
        }*/
        throw new SQLException("暂时不想引入mybatis，不支持此操作");
    }

    /**
     * 字符串取值增加单引号、日期格式化
     * 该方法只在内部使用，它偷偷将map内容给改变了，可能会给其他引用map的程序造成麻烦，请使用SqlUtils.formatMap(map)替代
     */
    private static void forSpecialValue(Map<String, Object> conditionMap) {
        conditionMap.keySet().forEach(key -> {
            Object value = conditionMap.get(key);
            if (value instanceof String) {
                conditionMap.put(key, "'".concat(value.toString()).concat("'"));
            }
            if (value instanceof Date) {
                String dateString = DateConverterFactory.getDateConverter().toStandardString((Date) value);
                conditionMap.put(key, "'".concat(dateString).concat("'"));
            }
            if (value instanceof Calendar) {
                String dateString = DateConverterFactory.getDateConverter().toStandardString((Calendar) value);
                conditionMap.put(key, "'".concat(dateString).concat("'"));
            }
            if (value instanceof Number) {
                conditionMap.put(key, value.toString());
            }
        });
    }

    public static Map formatMap(Map conditionMap) {
        Map formattedMap = new HashMap<>();
        conditionMap.keySet().forEach(key -> formattedMap.put(key, conditionMap.get(key)));
        forSpecialValue(formattedMap);
        return formattedMap;
    }

    /**
     * 将形如 xxx = {fieldName} 的语句全部转为 xxx is null
     * 注意，该算法修改了数组参数sqlFragments内的数组元素
     */
    public static String[] replaceEqualNullWithIsNull(String fieldName, String... sqlFragments) {
        String namedParam = "{" + fieldName + "}";
        for (int i = 0; i < sqlFragments.length; i++) {
            String fragment = sqlFragments[i];
            String equalNull = findEqualNamedParamPattern(fragment, namedParam);
            while (equalNull != null) {
                fragment = fragment.replaceAll(equalNull, " is null");//注意这里is null 前面的空格，很重要
                sqlFragments[i] = fragment;
                equalNull = findEqualNamedParamPattern(fragment, namedParam);
            }
        }
        return sqlFragments;
    }

    /**
     * 不会正则表达式 的 悲哀
     *
     * @return 如果包含形如 "= namedParam"的子字符串，返回第一个出现的那个字符串，反之返回null
     */
    private static String findEqualNamedParamPattern(String fragment, String namedParam) {
        if (fragment.contains(namedParam) && fragment.contains("=")) {
            int equalOperatorIndex = fragment.indexOf('=');
            int namedParameterIndex = fragment.indexOf(namedParam);
            if (equalOperatorIndex < namedParameterIndex) {
                String stringOfWhiteSpaces = fragment.substring(equalOperatorIndex + 1, namedParameterIndex);
                if (stringOfWhiteSpaces.trim().equals("")) {
                    return fragment.substring(equalOperatorIndex, namedParameterIndex + namedParam.length());
                }
            }
        }
        return null;
    }

    /**
     * 添加模糊查询逻辑，
     */
    public static String addFuzzyness(String value) {
        if (!StringUtil.isEmpty(value)) {
            if (!value.startsWith("%")) {
                value = "%" + value;
            }
            if (!value.endsWith("%")) {
                value += "%";
            }
        }
        return value;
    }

    /**
     * 将查询转为查询条数
     */
    public static String convertToQueryCount(String querySql) {
        //第一个from关键字
        return "select count(1) " + querySql.substring(querySql.indexOf(" from "));
    }

    public static String getMainAlias(String lowerCaseSql) {
        if (lowerCaseSql.contains("\n")) {
            lowerCaseSql = lowerCaseSql.replaceAll("\n", " ");
        }
        try {
            String table = SqlUtils.getTablesBetweenFromAndWhere(lowerCaseSql)[0];
            if (table.contains(" join ")) {
                table = table.substring(0, table.indexOf(" join ") + 1).replaceAll(" left | right | inner | outer ", "");
            }
            String[] splits = table.split(" as | ");
            return splits[splits.length - 1];
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Please check 'SelectFrom' annotation'value on the condition class.", e);
        }
    }

    private static String[] getTablesBetweenFromAndWhere(String sql) {
        int from = sql.indexOf(" from "), where = sql.indexOf(" where ");
        return sql.substring(from + 6, where).split(",");
    }

    /**
     * 将map填充到到sql模式串中
     * 使用此此方法要求map内的key-value完整性;而我们的需求是可空的key-value可以不在map内出现
     * 建议先使用DBUtil.xxxSQL然后再调用此方法
     */
    public static String mapToSql(String sql, Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            return MapFormat.format(sql, formatMap(map));
        }
        return sql;
    }
}
