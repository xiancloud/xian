package info.xiancloud.dao.core.units;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.select.ISelect;
import info.xiancloud.dao.core.action.select.SelectAction;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.ddl.TableHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * 公共查询byId dao unit
 *
 * @author hang
 */
abstract public class BaseQueryByIdDB extends DaoUnit {

    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{new SelectAction() {
            @Override
            protected Object selectList() {
                return "*";
            }

            @Override
            protected Object sourceTable() {
                Object tableName = getMap().get("$tableName");
                if (tableName instanceof String) {
                    return TableHeader.getTableName(getMap().get("$tableName").toString());
                }
                return tableName;
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

            @Override
            public int resultType() {
                return ISelect.SELECT_SINGLE;
            }
        }};
    }

    @Override
    public String getName() {
        return "BaseQueryByIdDB";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("公共查询byId dao unit");
    }

}
