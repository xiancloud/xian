package info.xiancloud.plugin.dao.core.utils.date;

/**
 * @author happyyangyuan
 */
public class DateConverterFactory {


    public static IDateConverter getDateConverter() {
        return new DateConverterForMySQL();
    }


}
