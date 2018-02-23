package info.xiancloud.plugin.dao.core.jdbc.router;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * data router by month
 *
 * @author happyyangyuan
 * @deprecated not tested
 */
public class RouterForRead extends AbstractRouter {

    private Integer monthCount = 3;

    public RouterForRead(int monthCount) {
        this.monthCount = monthCount;
    }

    private List<String> tables(String tableHeader) {
        Calendar now = new GregorianCalendar();
        List<String> tables = new ArrayList<>();
        for (int i = 0; i < monthCount; i++) {
            tables.add(IMonthTableRouter.getSingleTable(tableHeader, now));
            now.add(Calendar.MONTH, -1);
        }
        return tables;
    }

    private String singleSelect(String singleTable) {
        return " select * from ".concat(singleTable).concat(" ");
    }

    String getTable(String tableHeader) {
        String table = "";
        for (String singleTable : tables(tableHeader)) {
            table = table.concat("union ").concat(singleSelect(singleTable));
        }
        return "(".concat(table.substring("union".length())).concat(")");
    }

}
