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

/**
 * 公共修改ById dao unit
 */
abstract public class BaseUpdateByIdDB extends DaoUnit {
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
                    protected String[] searchConditions() {
                        Object tableName = getMap().get("$tableName");
                        if (tableName instanceof String) {
                            Table table = TableHeader.getTable(getMap().get("$tableName").toString());
                            if (table != null) {
                                String[] pkKeys = table.getPrimaryKey();
                                if (pkKeys != null) {
                                    List<String> pkList = new ArrayList<>();
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
        return UnitMeta.createWithDescription("公共修改ById dao unit");
    }

}