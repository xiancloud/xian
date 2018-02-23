package info.xiancloud.plugin.dao.core.jdbc.sql;

import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;
import info.xiancloud.plugin.util.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class SelectMinAction extends SelectAction/*OrderBySelectAction*/ {

    final protected String sqlTail(DaoUnit daoUnit, Map map, Connection connection) {
        String groupByClause = StringUtil.isEmpty(groupByClause()) ? " " : " ".concat(groupByClause()).concat(" ");
        return groupByClause.concat(ISelect.minQueryTail(minColumn()));
    }

    abstract protected String minColumn();

    @Override
    final protected String sqlHeader(Map map, Connection connection) {
        return super.sqlHeader(map, connection);
    }

    @Override
    final protected String sqlPattern(Map map, Connection connection) throws SQLException {
        return super.sqlPattern(map, connection);
    }

    /**
     * @return 应当返回完整的group by子句,或者穿空忽略group by
     */
    protected String groupByClause() {
        return "";
    }
}
