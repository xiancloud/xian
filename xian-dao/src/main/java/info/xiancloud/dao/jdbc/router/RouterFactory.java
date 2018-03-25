package info.xiancloud.dao.jdbc.router;


import info.xiancloud.dao.jdbc.sql.Action;
import info.xiancloud.dao.jdbc.sql.ISelect;

/**
 * @author happyyangyuan
 */
public class RouterFactory {

    public static Integer MONTH_COUNT = 3;

    public static IMonthTableRouter getRouter(Action action) {
        if (action instanceof ISelect) {
            return new RouterForRead(MONTH_COUNT);
        }
        return new RouterForWrite();
    }
}
