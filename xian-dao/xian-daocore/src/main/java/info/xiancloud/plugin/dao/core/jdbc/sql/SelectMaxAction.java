package info.xiancloud.plugin.dao.core.jdbc.sql;

import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;
import info.xiancloud.plugin.util.StringUtil;

import java.sql.Connection;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class SelectMaxAction extends SelectAction/*OrderBySelectAction*/ {

    final protected String sqlTail(DaoUnit daoUnit, Map map, Connection connection) {
        String groupByClause = StringUtil.isEmpty(groupByClause()) ? " " : " ".concat(groupByClause()).concat(" ");
        return groupByClause.concat(ISelect.maxQueryTail(maxColumn()));
    }

    abstract protected String maxColumn();

    /**
     * @return 应当返回完整的group by子句,或者穿空忽略group by
     */
    protected String groupByClause() {
        return "";
    }
}
