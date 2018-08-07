package info.xiancloud.dao.jdbc.router;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author happyyangyuan
 */
public interface IMonthTableRouter {

    String getRoutedSql(String sql);

    static String getSingleTable(String tableHeader, Calendar date) {
        String table = tableHeader;
        String month = date.get(GregorianCalendar.MONTH) >= 10 ? date.get(GregorianCalendar.MONTH) + "" : "0" + date.get(GregorianCalendar.MONTH),
                year = date.get(GregorianCalendar.YEAR) + "";
        if (!table.endsWith("_")) {
            table = table.concat("_");
        }
        return table.concat(year).concat(month);
    }

}
