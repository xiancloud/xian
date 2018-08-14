package info.xiancloud.dao.group.unit.base;

import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.ddl.TableHeader;
import info.xiancloud.dao.group.unit.DaoUnit;
import info.xiancloud.dao.jdbc.sql.Action;
import info.xiancloud.dao.jdbc.sql.ISelect;
import info.xiancloud.dao.jdbc.sql.QueryAction;

import java.util.ArrayList;
import java.util.List;

/**
 * 公共查询byId dao unit
 *
 * @author hang
 */
abstract public class BaseQueryByIdDB extends DaoUnit {

    @Override
    public Action[] getActions() {
        return new Action[]{
                new QueryAction() {
                    @Override
                    public int resultType() {
                        return ISelect.SELECT_SINGLE;
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
                }
        };
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
