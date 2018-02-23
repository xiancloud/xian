package info.xiancloud.plugin.dao.core.utils.date;

import java.util.Calendar;
import java.util.Date;

/**
 * @author happyyangyuan
 */
public interface IDateConverter {

    String toStandardString(Date date);

    Date parse(String dateString);

    String toStandardString(Calendar calendar);

}
