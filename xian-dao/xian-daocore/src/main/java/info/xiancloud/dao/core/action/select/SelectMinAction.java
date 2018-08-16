package info.xiancloud.dao.core.action.select;

import info.xiancloud.core.util.StringUtil;

/**
 * SelectMinAction, eg.
 * <code>
 * <p>
 * select min(col0) from table0 where col1 = 123
 * group by col2
 * </p>
 * </code>
 *
 * @author happyyangyuan
 * @deprecated Too many sql action implementations event make things complicated. Use {@link SelectAction select action} instead and write your own sql clause.
 */
public abstract class SelectMinAction extends SelectAction/*OrderBySelectAction*/ {

    @Override
    final protected String sqlTail() {
        String groupByClause = StringUtil.isEmpty(groupByClause()) ? " " : " ".concat(groupByClause()).concat(" ");
        return groupByClause.concat(ISelect.minQueryTail(minColumn()));
    }

    /**
     * get the column name parameter for min function
     *
     * @return column name parameter for min function
     */
    abstract protected String minColumn();

    /**
     * @return 应当返回完整的group by子句,或者穿空忽略group by
     */
    protected String groupByClause() {
        return "";
    }
}
