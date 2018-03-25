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

/**
 * 公共修改ById dao unit
 */
abstract public class BaseUpdateByIdDB extends DaoUnit {
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
                        Object tableName = map.get("$tableName");
                        if (tableName instanceof String) {
                            Table table = TableHeader.getTable(map.get("$tableName").toString());
                            if (table != null) {
                                String[] pkKeys = table.getPrimaryKey();
                                if (pkKeys != null) {
                                    List<String> pkList = new ArrayList<String>();
                                    for (String pk : pkKeys) {
                                        pkList.add(String.format("%s = {%s}", pk, StringUtil.underlineToCamel(pk)));
                                    }
                                    return pkList.toArray(new String[]{});
                                }
                            }
                        }
                        return new String[]{"1 < 0"};
                    }
                }
        };
    }

    @Override
    public String getName() {
        return "BaseUpdateByIdDB";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("公共修改ById dao unit");
    }

}