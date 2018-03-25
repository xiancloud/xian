package info.xiancloud.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utility for general usage.
 *
 * @author happyyangyuan
 */
public class StringUtil {

    public static boolean isEmpty(Object value) {
        return null == value || value.equals("");
    }

    private static final String UNDERLINE = "_";

    private static final int INDEX_NOT_FOUND = -1;

    /**
     * convert camel style string to underline style.
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * convert underline style string to camel style.
     */
    public static String underlineToCamel(String param) {
        if (isEmpty(param.trim())) {
            return "";
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile(UNDERLINE).matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }

    /**
     * string keys are converted from camel style to underline style. a new map is returned, the original map is not changed.
     */
    public static Map<String, Object> camelToUnderline(Map<String, Object> map) {
        Map<String, Object> newMap = new HashMap<>();
        for (String key : map.keySet()) {
            newMap.put(camelToUnderline(key), map.get(key));
        }
        return newMap;
    }

    /**
     * the opposite with {@link #camelToUnderline(Map)}
     */
    public static Map<String, Object> underlineToCamel(Map<String, Object> map) {
        Map<String, Object> newMap = new HashMap<>();
        for (String key : map.keySet()) {
            newMap.put(underlineToCamel(key), map.get(key));
        }
        return newMap;
    }

    /**
     * Judge whether the given decimal number's decimal length is in the given range.
     * Talk is cheap, see the code.
     *
     * @param value the string represents the decimal number.
     * @param digit the range for the decimal length.
     */
    public static boolean isFitDigit(String value, int digit) {
        return (value.split("\\.")[1].length() <= digit);
    }

    /**
     * get the stacktrace string.
     *
     * @param t the throwable object.
     * @return the stacktrace string.
     */
    public static String getExceptionStacktrace(Throwable t) {
        try (StringWriter errors = new StringWriter()) {
            t.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        } catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }

    /**
     * reverseString
     */
    public static String reverseString(String str) {
        StringBuilder stringBuffer = new StringBuilder(str);
        return stringBuffer.reverse().toString();
    }

    /**
     * delete the comment things in the given string.
     */
    public static String removeComment(String text) {
        return text.replaceAll("(?<!:)\\/\\/.*|\\/\\*(\\s|.)*?\\*\\/", "");
    }

    /**
     * Find the given object's index in the given array.
     * Something like the {@link String#indexOf(String)}
     */
    public static int indexOf(final Object[] array, final Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    /**
     * Find the given object's index in the given array.
     * Something like the {@link String#indexOf(String)}
     */
    public static int indexOf(final Object[] array, final Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * escape the regex special character: （$()*+.[]?\^{},|）
     */
    public static String escapeSpecialChar(String keyword) {
        if (!isEmpty(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }


    /**
     * firstCharToLowerCase
     */
    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * firstCharToUpperCase
     */
    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    public static boolean notNull(Object... paras) {
        if (paras == null)
            return false;
        for (Object obj : paras)
            if (obj == null)
                return false;
        return true;
    }

    /**
     * split the given string into an array with the given splitter.
     * note that all elements in the returned array are trimmed.
     *
     * @param str         the string to be split, if null the split result is en empty string array.
     * @param theSplitter theSplitter, regex special character will be escaped.
     * @return spitted array or empty array if the input string is empty.
     */
    public static String[] split(String str, String theSplitter) {
        if (StringUtil.isEmpty(str)) {
            return new String[0];
        }
        return str.trim().split("\\s*" + escapeSpecialChar(theSplitter) + "\\s*");
    }

    /**
     * create a random num
     */
    public static String createNum(int length) {
        return RandomUtils.getRandomNumbers(length);
    }
}
