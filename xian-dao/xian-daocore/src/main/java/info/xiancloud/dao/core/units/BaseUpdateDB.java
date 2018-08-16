package info.xiancloud.dao.core.units;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.update.UpdateAction;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.ddl.TableHeader;

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
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new UpdateAction() {
                    private Table table;

                    private Table getTable(Map map) {
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
                    public String tableName() {
                        Table table = getTable(getMap());
                        return table == null ? "" : table.getName();
                    }

                    @Override
                    public String[] unique() {
                        Table table = getTable(getMap());
                        return table == null ? new String[]{} : table.getUnique();
                    }

                    @Override
                    protected String[] searchConditions() {
                        List<String> whereList = new ArrayList<>();
                        //$tableName is table alias
                        Object tableName = getMap().get("$tableName");
                        if (tableName instanceof String) {
                            Map<String, Class<?>> columns = TableHeader.getTableColumnTypeMap(tableName.toString(), getSqlDriver());
                            for (Entry<String, Class<?>> entry : columns.entrySet()) {
                                String key = entry.getKey();
                                if (getMap().containsKey(StringUtil.underlineToCamel(key))) {
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
        return UnitMeta.createWithDescription("公共修改 dao unit");
    }

}