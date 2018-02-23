package info.xiancloud.plugin.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Helper class to deal with Java Date.
 *
 * @author happyyangyuan
 */
public class DateUtil {

    public final static String dateFormat = "yyyy-MM-dd";
    public final static String dateNumberFormat = "yyyyMMdd";
    public final static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    public final static String dateTimeNumberFormat = "yyyyMMddHHmmss";
    public final static String timeFormat = "HH:mm:ss";
    public final static String timeNumberFormat = "HHmmss";

    public static String toDateStr(Date date) {
        return toStr(date, dateFormat);
    }

    public static String toDateTimeStr(Date date) {
        return toStr(date, dateTimeFormat);
    }

    public static String toTimeStr(Date date) {
        return toStr(date, timeFormat);
    }

    public static String getDateStr() {
        return toStr(new Date(), dateFormat);
    }

    public static String getDateTimeStr() {
        return toStr(new Date(), dateTimeFormat);
    }

    public static String getTimeStr() {
        return toStr(new Date(), timeFormat);
    }

    public static String getDateNumberStr() {
        return toStr(new Date(), dateNumberFormat);
    }

    public static String getDateTimeNumberStr() {
        return toStr(new Date(), dateTimeNumberFormat);
    }

    public static String getTimeNumberStr() {
        return toStr(new Date(), timeNumberFormat);
    }

    public static String toStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date toDate(String dateStr) {
        return toDate(dateStr, dateTimeFormat);
    }

    public static Date toDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            LOG.error(e);
        }
        return null;
    }

    public static Date addSecond(Date date, int addNumber) {
        return add(date, Calendar.SECOND, addNumber);
    }

    public static Date addMinute(Date date, int addNumber) {
        return add(date, Calendar.MINUTE, addNumber);
    }

    public static Date addHour(Date date, int addNumber) {
        return add(date, Calendar.HOUR, addNumber);
    }

    public static Date addDate(Date date, int addNumber) {
        return add(date, Calendar.DATE, addNumber);
    }

    public static Date addMonth(Date date, int addNumber) {
        return add(date, Calendar.MONTH, addNumber);
    }

    public static Date addYear(Date date, int addNumber) {
        return add(date, Calendar.YEAR, addNumber);
    }

    /**
     * @param date
     * @param addType   例如:Calendar.DATE,Calendar.MONDAY...
     * @param addNumber 加为正数，减为负数
     * @return
     */
    public static Date add(Date date, int addType, int addNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(addType, addNumber);
        return calendar.getTime();
    }

    /**
     * 将以yyyy-MM-dd开头的日期字符串格式化为yyyy-MM-dd短日期格式
     *
     * @param strDate eg. 2015-10-11 11:11:11
     * @return eg. 2015-10-11 12:12:12  to  2015-10-11
     */
    public static String yyyy_MM_dd(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        SimpleDateFormat formatter2 = new SimpleDateFormat(dateFormat);
        return formatter2.format(strtodate);
    }

    /**
     * 将日期格式的字符串转换为长整型
     */
    public static long convert2long(String date, String format) {
        try {
            if (!StringUtil.isEmpty(date)) {
                if (StringUtil.isEmpty(format))
                    format = dateTimeFormat;
                SimpleDateFormat sf = new SimpleDateFormat(format);
                return sf.parse(date).getTime();
            }
        } catch (ParseException e) {
            LOG.error(e);
        }
        return 0l;
    }

    /**
     * 将长整型数字转换为日期格式的字符串
     */
    public static String convert2String(long time, String format) {
        if (time > 0l) {
            if (StringUtil.isEmpty(format))
                format = dateTimeFormat;
            SimpleDateFormat sf = new SimpleDateFormat(format);
            Date date = new Date(time);
            return sf.format(date);
        }
        return "";
    }

}
