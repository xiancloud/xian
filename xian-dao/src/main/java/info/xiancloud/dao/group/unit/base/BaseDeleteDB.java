package info.xiancloud.dao.group.unit.base;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.global.Table;
import info.xiancloud.dao.global.TableHeader;
import info.xiancloud.dao.group.unit.DaoUnit;
import info.xiancloud.dao.jdbc.sql.Action;
import info.xiancloud.dao.jdbc.sql.DeleteAction;
import info.xiancloud.dao.jdbc.sql.QueryAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 公共批量删除 dao unit
 */
abstract public class BaseDeleteDB extends DaoUnit {
    @Override
    public Action[] getActions() {
        return new Action[]{new DeleteAction() {
            @Override
            public String table() {
                Object tableName = map.get("$tableName");
                if (tableName instanceof String) {
                    Table table = TableHeader.getTable(map.get("$tableName").toString());
                    if (table.getType().equals(Table.Type.view)) {
                        throw new RuntimeException(String.format("视图:%s,不允许操作 BaseDeleteDB", table.getName()));
                    }
                    return table.getName();
                }
                return "";
            }

            @Override
            protected String[] where() {
                List<String> whereList = new ArrayList<String>();
                Object tableName = map.get("$tableName");
                if (tableName instanceof String) {
                    Map<String, Class<?>> columns = TableHeader.getTableColumnTypeMap(tableName.toString(), connection);
                    for (Entry<String, Class<?>> entry : columns.entrySet()) {
                        String key = entry.getKey();
                        Object value = map.get(StringUtil.underlineToCamel(key));
                        if (value != null) {
                            String[] array = QueryAction.fmtObjectToStrArray(value);
                            if (array != null) {
                                whereList.add(String.format("%s in {%s}", key, StringUtil.underlineToCamel(key)));
                            } else {
                                whereList.add(String.format("%s = {%s}", key, StringUtil.underlineToCamel(key)));
                            }
                        }
                    }
                }
                //这里不允许执行整表删除功能
                if (whereList.size() > 0) {
                    return whereList.toArray(new String[]{});
                }
                return new String[]{"1 < 0"};
            }
        }};
    }

    @Override
    public String getName() {
        return "BaseDeleteDB";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("公共批量删除Dao unit");
    }

}