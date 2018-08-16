package info.xiancloud.dao.core.action.select;

import info.xiancloud.core.util.StringUtil;

/**
 * Order by selection action
 *
 * @author happyyangyuan
 */
public abstract class OrderBySelectAction extends SelectAction {

    @Override
    final protected String sqlTail() {
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
