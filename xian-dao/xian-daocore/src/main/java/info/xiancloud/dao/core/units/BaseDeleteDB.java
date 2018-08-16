package info.xiancloud.dao.core.units;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.delete.DeleteAction;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.ddl.TableHeader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 公共批量删除 dao unit
 *
 * @author happyyangyuan
 */
abstract public class BaseDeleteDB extends DaoUnit {
    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new DeleteAction() {
                    @Override
                    public String tableName() {
                        Object tableName = getMap().get("$tableName");
                        if (tableName instanceof String) {
                            Table table = TableHeader.getTable(getMap().get("$tableName").toString());
                            if (table.getType().equals(Table.Type.view)) {
                                throw new RuntimeException(String.format("视图:%s,不允许操作 BaseDeleteDB", table.getName()));
                            }
                            return table.getName();
                        }
                        return "";
                    }

                    @Override
                    protected String[] searchConditions() {
                        List<String> whereList = new ArrayList<>();
                        Object tableName = getMap().get("$tableName");
                        if (tableName instanceof String) {
                            Map<String, Class<?>> columns = TableHeader.getTableColumnTypeMap(tableName.toString(), getSqlDriver());
                            for (Entry<String, Class<?>> entry : columns.entrySet()) {
                                String key = entry.getKey();
                                Object value = getMap().get(StringUtil.underlineToCamel(key));
                                if (value != null) {
                                    String[] array = fmtObjectToStrArray(value);
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

    /**
     * cast or convert the given object to string array.
     *
     * @param object a string array or string collection.
     * @return string array
     */
    private static String[] fmtObjectToStrArray(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String[]) {
            return (String[]) object;
        }
        if (object instanceof Collection) {
            Collection c = (Collection) object;
            return (String[]) c.toArray(new String[c.size()]);
        }
        return null;
    }

}