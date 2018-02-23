package info.xiancloud.plugin.dao.core.utils.date;


import info.xiancloud.plugin.util.LOG;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author happyyangyuan
 */
public class DateConverterForMySQL implements IDateConverter {

    public static String FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public String toStandardString(Date date) {
        DateFormat df = new SimpleDateFormat(FORMAT);
        return df.format(date);
    }

    public Date parse(String dateString) {
        DateFormat df = new SimpleDateFormat(FORMAT);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            LOG.error("ERROR Date format " + dateString);
            return null;
        }
    }

    @Override
    public String toStandardString(Calendar calendar) {
        return toStandardString(calendar.getTime());
    }

    public static void main(String... args) {
        new DateConverterForMySQL().toStandardString(new GregorianCalendar());
    }
}
