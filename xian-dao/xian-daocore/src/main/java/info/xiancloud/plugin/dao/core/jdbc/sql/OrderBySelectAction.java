package info.xiancloud.plugin.dao.core.jdbc.sql;

import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;
import info.xiancloud.plugin.util.StringUtil;

import java.sql.Connection;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class OrderBySelectAction extends SelectAction {

    final protected String sqlTail(DaoUnit daoUnit, Map map, Connection connection) {
        String groupByClause = StringUtil.isEmpty(groupByClause()) ? " " : " ".concat(groupByClause()).concat(" ");
        return groupByClause.concat(ISelect.orderByPagingQueryTail(ascOrDesc(), orderByColumn(), null, null));
    }

    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    abstract protected String ascOrDesc();

    abstract protected String orderByColumn();

    /**
     * @return 应当返回完整的group by子句,或者穿空忽略group by
     */
    protected String groupByClause() {
        return "";
    }

}
