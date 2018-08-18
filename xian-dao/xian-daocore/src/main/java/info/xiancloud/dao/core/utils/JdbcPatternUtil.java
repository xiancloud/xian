package info.xiancloud.dao.core.utils;

import info.xiancloud.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * utility for sql pattern
 *
 * @author happyyangyuan
 */
public class JdbcPatternUtil {

    /**
     * 得到sql模式串中的原模原样的key
     */
    protected static List<String> getOriginalKeys(String sqlFragment) {
        List<String> keys = new ArrayList<String>();
        if (sqlFragment.contains("{")) {
            String key = sqlFragment.substring(sqlFragment.indexOf('{') + 1, sqlFragment.indexOf('}')).trim();
            keys.add(key);
            keys.addAll(getOriginalKeys(sqlFragment.substring(sqlFragment.indexOf('}') + 1)));
        }
        return keys;
    }

    private static String getFirstCamelKey(String sqlFragment) {
        if (sqlFragment.contains("{")) {
            String key = sqlFragment.substring(sqlFragment.indexOf('{') + 1, sqlFragment.indexOf('}')).trim();
            return StringUtil.underlineToCamel(key);
        }
        return "";
    }

    /**
     * 得到{}扩住的key，并一律转为驼峰式
     */
    public static List<String> getCamelKeys(String pattern) {
        List<String> keys = new ArrayList<String>();
        for (String s : getOriginalKeys(pattern)) {
            keys.add(StringUtil.underlineToCamel(s));
        }
        return keys;
    }

    public static List getValues(String sqlPattern, Map<String, Object> map) {
        List<Object> values = new ArrayList<>();
        for (String camelKey : getCamelKeys(sqlPattern)) {
            values.add(map.get(camelKey));
        }
        return values;
    }

    public static List getValues(List<String> camelKeys, Map<String, Object> map) {
        List<Object> values = new ArrayList<>();
        for (String camelKey : camelKeys) {
            values.add(map.get(camelKey));
        }
        return values;
    }

    //兼容{}内出现下划线的情况
    public static String bareError(String pattern) {
        List<String> camelKeys = getCamelKeys(pattern),
                orignalKeys = getOriginalKeys(pattern);
        for (int i = 0; i < camelKeys.size(); i++) {
            pattern = pattern.replaceFirst("\\{" + orignalKeys.get(i) + "\\}", "{" + camelKeys.get(i) + "}");
        }
        return pattern;
    }

    public static void adjustLikeClause(String pattern, Map<String, Object> map) {
        String lowerCase = pattern.toLowerCase();
        String like = " like ";
        int index = lowerCase.indexOf(like);
//        String regex = "\\s{1,}like\\s{0,}\\{[^}]*\\}";
        while (index != -1) {
            index = lowerCase.indexOf(like, index + like.length());
            String subString = pattern.substring(index + like.length());
            String camelKey = getFirstCamelKey(subString);
            String likeValue = map.get(camelKey).toString();
            if (!likeValue.startsWith("%") && !likeValue.endsWith("%")) {
                map.put(camelKey, "%".concat(likeValue).concat("%"));
            }
        }
    }

    public static String getPreparedSql(String sqlPattern) {
        return sqlPattern.replaceAll("\\{[^}]*\\}", "?");
    }

    /**
     * 根据sqlPattern,获取其对应的preparedSql的参数
     */
    public static Object[] getSqlParams(String sqlPattern, Map map) {
        Object[] sqlParams = JdbcPatternUtil.getValues(JdbcPatternUtil.getCamelKeys(sqlPattern), map).toArray();
        for (int i = 0; i < sqlParams.length; i++) {
            if (sqlParams[i] instanceof Calendar) {//因为prepared statement不支持calendar类型,所以需要将其其中的calendar转为date
                sqlParams[i] = ((Calendar) sqlParams[i]).getTime();
            }
        }
        return sqlParams;
    }

    public static void main(String... args) {
        String sql = "`userId`	\n `merchantInfo`	\n `openId`	\n `userName`	\n `password`	\n `realName`	\n `remark`	\n `parentUserId`	\n `phoneNum`	\n `email`	\n `status`	\n `joinTime`	\n `inactiveTime`	\n `activeTime`	\n `companyShortName`	\n `companyFullName`	\n `companyPrincipal`	\n `companyAddress`	\n `companyTel`	\n `companyOperator`	\n `companyOperatorTel`	\n `companyType`	\n `companyWxMchId`	\n `companyIsWxSign`	\n `payoffWay`	\n `innerSell`	\n `appId`	\n `appShortName`	\n `regionalCode`	\n `addr`	\n `createTime`	\n `createUser`	\n `lastUpdateUser`	\n `lastUpdateTime`	\n";
        System.out.println(StringUtil.camelToUnderline(sql));
    }
}
