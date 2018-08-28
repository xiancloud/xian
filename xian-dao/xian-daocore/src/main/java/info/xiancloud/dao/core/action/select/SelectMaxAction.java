package info.xiancloud.dao.core.action.select;

import info.xiancloud.core.util.StringUtil;

/**
 * SelectMaxAction
 * <code>
 * select max(col0) from table0 where col1 = 123
 * group by col2
 * </code>
 *
 * @author happyyangyuan
 * @deprecated Too many sql action implementations event make things complicated. Use {@link SelectAction select action} instead and write your own sql clause.
 */
public abstract class SelectMaxAction extends SelectAction/*OrderBySelectAction*/ {

    @Override
    final protected String sqlTail() {
        String groupByClause = StringUtil.isEmpty(groupByClause()) ? " " : " ".concat(groupByClause()).concat(" ");
        return groupByClause.concat(ISelect.maxQueryTail(maxColumn()));
    }

    /**
     * get column name parameter for sql max function
     *
     * @return column name parameter for sql max function
     */
    abstract protected String maxColumn();

    /**
     * get the group by clause
     *
     * @return full group by clause or null or empty string
     */
    protected String groupByClause() {
        return "";
    }
}
