package info.xiancloud.plugin.util;

import java.util.List;
import java.util.Random;

/**
 * 随机数工具类
 *
 * @author Trinea 2012-5-12 下午01:37:48
 */
public class RandomUtils {
    public static final String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
    public static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 得到固定长度的随机字符串，字符串由数字和大小写字母混合组成
     *
     * @param length 长度
     */
    public static String getRandomNumbersAndLetters(int length) {
        return getRandom(NUMBERS_AND_LETTERS, length);
    }

    /**
     * 得到固定长度的随机字符串，字符串由数字混合组成
     *
     * @param length 长度
     */
    public static String getRandomNumbers(int length) {
        return getRandom(NUMBERS, length);
    }

    /**
     * 得到固定长度的随机字符串，字符串由大小写字母混合组成
     *
     * @param length 长度
     */
    public static String getRandomLetters(int length) {
        return getRandom(LETTERS, length);
    }

    /**
     * 得到固定长度的随机字符串，字符串由大写字母混合组成
     *
     * @param length 长度
     */
    public static String getRandomCapitalLetters(int length) {
        return getRandom(CAPITAL_LETTERS, length);
    }

    /**
     * 得到固定长度的随机字符串，字符串由小写字母混合组成
     *
     * @param length 长度
     */
    public static String getRandomLowerCaseLetters(int length) {
        return getRandom(LOWER_CASE_LETTERS, length);
    }

    /**
     * 得到固定长度的随机字符串，字符串由source中字符混合组成
     *
     * @param source 源字符串
     * @param length 长度
     */
    public static String getRandom(String source, int length) {
        return StringUtil.isEmpty(source) ? null : getRandom(source.toCharArray(), length);
    }

    /**
     * 得到固定长度的随机字符串，字符串由sourceChar中字符混合组成
     *
     * @param sourceChar 源字符数组
     * @param length     长度
     * @return <ul>
     * <li>若sourceChar为null或长度为0，返回null</li>
     * <li>若length小于0，返回null</li>
     * </ul>
     */
    public static String getRandom(char[] sourceChar, int length) {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }
        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(sourceChar[random.nextInt(sourceChar.length)]);
        }
        return str.toString();
    }

    /**
     * 从列表中随机返回一个元素
     */
    public static Object getRandom(List<Object> sources) {
        Random random = new Random();
        int index = random.nextInt(sources.size());
        return sources.get(index);
    }

    /**
     * 从数组中获取随机元素
     */
    public static Object getRandom(Object... array) {
        Random random = new Random();
        int index = random.nextInt(array.length);
        return array[index];
    }
}