//请不要修改TableHeader的包路径，它是用来覆盖xian-db_core.jar中的TableHeader类的，所以包路径必须完全一致
package info.xiancloud.dao.core.model.ddl;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.sql.ISingleTableSqlDriver;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Moved to xian-dbCommon module by yy @2017-05-08
 * 请不要修改xian-dbCommon module内的TableHeader的类名，它是用来覆盖xian-db_core.jar中的TableHeader类的，所以类名必须完全一致。
 * db_core中的这个TableHeader类主要用于框架本地测试使用。
 *
 * @author happyyangyuan
 */
public interface TableHeader {

    /**
     * get {@link Table} from table name or table name alias
     *
     * @param name simple table name( without database prefix) or table name alias
     * @return table object
     */
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

    /**
     * get simple table name ( without database prefix)
     *
     * @param name simple table name( without database prefix) or table name alias
     * @return simple table name without database prefix
     */
    static String getTableName(String name) {
        Table table = getTable(name);
        if (table != null) {
            return table.getName();
        }
        return name;
    }

    /**
     * get the specified table's column name and column type key-value map.
     *
     * @param name      simple table name( without database prefix) or table name alias
     * @param sqlDriver the sql driver instance
     * @return table column name and type key-value map
     */
    static Map<String, Class<?>> getTableColumnTypeMap(String name, ISingleTableSqlDriver sqlDriver) {
        Table table = getTable(name);
        if (table != null) {
            Map<String, Class<?>> columnTypeMap = table.getColumnTypeMap();
            if (columnTypeMap == null || columnTypeMap.size() == 0) {
                //todo so here if table metadata has changed, we have to restart java application to reload the table metadata.
                sqlDriver.buildTableMetaData(table).subscribe();
                return table.getColumnTypeMap();
            }
            return table.getColumnTypeMap();
        }
        return null;
    }

}
