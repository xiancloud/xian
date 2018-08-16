package info.xiancloud.dao.core.units;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.insert.InsertAction;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.ddl.TableHeader;

import java.util.Map;

/**
 * 公共新增 dao unit
 *
 * @author hang
 */
abstract public class BaseAddDB extends DaoUnit {
    @Override
    public String getName() {
        return "BaseAddDB";
    }

    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new InsertAction() {
                    private Table table;

                    @Override
                    public String tableName() {
                        Table table = getTable(getMap());
                        return table == null ? "" : table.getName();
                    }

                    private Table getTable(Map map) {
                        if (table != null) {
                            return table;
                        }
                        Object tableName = map.get("$tableName");
                        if (tableName instanceof String) {
                            table = TableHeader.getTable(map.get("$tableName").toString());
                            if (table.getType().equals(Table.Type.view)) {
                                throw new RuntimeException(String.format("视图:%s,不允许操作 BaseAddDB", table.getName()));
                            }
                            return table;
                        }
                        return null;
                    }
                }
        };
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("公共新增dao unit").setDocApi(false);
    }

}