package info.xiancloud.dao.group.unit.base;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.ddl.TableHeader;
import info.xiancloud.dao.group.unit.DaoUnit;
import info.xiancloud.dao.jdbc.sql.Action;
import info.xiancloud.dao.jdbc.sql.DeleteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * 公共删除ById dao unit
 */
abstract public class BaseDeleteByIdDB extends DaoUnit {
    @Override
    public String getName() {
        return "BaseDeleteByIdDB";
    }

    @Override
    public Action[] getActions() {
        return new Action[]{new DeleteAction() {
            @Override
            public String table() {
                Object tableName = map.get("$tableName");
                if (tableName instanceof String) {
                    Table table = TableHeader.getTable(map.get("$tableName").toString());
                    if (table.getType().equals(Table.Type.view)) {
                        throw new RuntimeException(String.format("视图:%s,不允许操作 BaseDeleteByIdDB", table.getName()));
                    }
                    return table.getName();
                }
                return "";
            }

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
        }};
    }


    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("公共删除ById unit dao");
    }

}