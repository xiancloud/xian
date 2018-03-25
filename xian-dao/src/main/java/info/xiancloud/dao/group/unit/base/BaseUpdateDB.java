package info.xiancloud.dao.group.unit.base;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.global.Table;
import info.xiancloud.dao.global.TableHeader;
import info.xiancloud.dao.group.unit.DaoUnit;
import info.xiancloud.dao.jdbc.sql.Action;
import info.xiancloud.dao.jdbc.sql.UpdateAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 公共修改 dao unit
 */
abstract public class BaseUpdateDB extends DaoUnit {
    @Override
    public String getName() {
        return "BaseUpdateDB";
    }

    @Override
    public Action[] getActions() {
        return new Action[]{
                new UpdateAction() {
                    private Table table;

                    public Table getTable(Map map) {
                        if (table != null) {
                            return table;
                        }
                        Object tableName = map.get("$tableName");
                        if (tableName instanceof String) {
                            table = TableHeader.getTable(map.get("$tableName").toString());
                            if (table.getType().equals(Table.Type.view)) {
                                throw new RuntimeException(String.format("视图:%s,不允许操作 BaseUpdateDB", table.getName()));
                            }
                            return table;
                        }
                        return null;
                    }

                    @Override
                    public String table() {
                        Table table = getTable(map);
                        return table == null ? "" : table.getName();
                    }

                    @Override
                    public String[] unique() {
                        Table table = getTable(map);
                        return table == null ? new String[]{} : table.getUnique();
                    }

                    ;

                    @Override
                    protected String[] where() {
                        List<String> whereList = new ArrayList<String>();
                        Object tableName = map.get("$tableName");
                        if (tableName instanceof String) {
                            Map<String, Class<?>> columns = TableHeader.getTableColumnTypeMap(tableName.toString(), connection);
                            for (Entry<String, Class<?>> entry : columns.entrySet()) {
                                String key = entry.getKey();
                                if (map.containsKey(StringUtil.underlineToCamel(key))) {
                                    whereList.add(String.format("%s = {%s}", key, StringUtil.underlineToCamel(key)));
                                }
                            }
                        }
                        //这里不允许执行整表修改功能
                        if (whereList.size() > 0) {
                            return whereList.toArray(new String[]{});
                        }
                        return new String[]{"1 < 0"};
                    }
                }
        };
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("公共修改 dao unit");
    }

}