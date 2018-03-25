package info.xiancloud.dao.utils.date;

/**
 * @author happyyangyuan
 */
public class DateConverterFactory {


    public static IDateConverter getDateConverter() {
        return new DateConverterForMySQL();
    }


}
