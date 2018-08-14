//请不要修改TableHeader的包路径，它是用来覆盖xian-db_core.jar中的TableHeader类的，所以包路径必须完全一致
package info.xiancloud.dao.core.model.ddl;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.jdbc.sql.ISingleTableAction;

import java.sql.Connection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by yhLiu.
 * Moved to xian-dbCommon module by yy @2017-05-08
 * 请不要修改xian-dbCommon module内的TableHeader的类名，它是用来覆盖xian-db_core.jar中的TableHeader类的，所以类名必须完全一致。
 * db_core中的这个TableHeader类主要用于框架本地测试使用。
 */
public interface TableHeader {
    ;

    static Table getTable(String name) {
        if (!StringUtil.isEmpty(name)) {
            //通过name
            for (String key : TableMapping.me().getMappings().keySet()) {
                if (name.equals(key)) {
                    return TableMapping.me().getMappings().get(key);
                }
            }
            //通过tableName
            for (Entry<String, Table> tab : TableMapping.me().getMappings().entrySet()) {
                if (name.equals(tab.getValue().getName())) {
                    return tab.getValue();
                }
            }
        }
        return null;
        //throw new RuntimeException(String.format("%s没有配置", name));
    }

    static String getTableName(String name) {
        Table table = getTable(name);
        if (table != null) {
            return table.getName();
        }
        return name;
    }

    static Map<String, Class<?>> getTableColumnTypeMap(String name, Connection connection) {
        Table table = getTable(name);
        if (table != null) {
            Map<String, Class<?>> columnTypeMap = table.getColumnTypeMap();
            if (columnTypeMap == null || columnTypeMap.size() == 0) {
                ISingleTableAction.doBuildTable(table, connection);
                return table.getColumnTypeMap();
            }
            return table.getColumnTypeMap();
        }
        return null;
    }
}
