package info.xiancloud.dao.core.units;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.delete.DeleteAction;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.ddl.TableHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * deletion ById dao unit
 *
 * @author happyyangyuan
 */
abstract public class BaseDeleteByIdDB extends DaoUnit {

    @Override
    public String getName() {
        return "BaseDeleteByIdDB";
    }

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
                                throw new RuntimeException(String.format("视图:%s,不允许操作 BaseDeleteByIdDB", table.getName()));
                            }
                            return table.getName();
                        }
                        return "";
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
                }};
    }


    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("公共删除ById unit dao");
    }

}