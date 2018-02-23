package info.xiancloud.plugin.dao.core.jdbc.router;

import java.util.GregorianCalendar;

/**
 * @author happyyangyuan
 * @deprecated not tested
 */
public class RouterForWrite extends AbstractRouter {

    String getTable(String tableHeader) {
        return IMonthTableRouter.getSingleTable(tableHeader, new GregorianCalendar());
    }

}
