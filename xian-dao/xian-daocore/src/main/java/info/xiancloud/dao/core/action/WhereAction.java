package info.xiancloud.dao.core.action;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.utils.PatternUtil;

import java.util.Collection;
import java.util.Map;

/**
 * 本类单一职责：拼装where条件.
 * Any actions with where clause should extends this class,
 * such as {@link info.xiancloud.dao.core.action.update.UpdateAction},
 * {@link info.xiancloud.dao.core.action.select.SelectAction}
 *
 * @author happyyangyuan
 */
public abstract class WhereAction extends AbstractSqlAction {

    @Override
    protected final String patternSql() {
        return sqlHeader().concat(buildWhere(getMap())).concat(" ").concat(sqlTail());
    }

    /**
     * produces the sql header. <br/>
     * eg. <code>select * from source_table</code><br/>
     * <code> update table0 set col0=123 </code><br/>
     * Note that where clause is not part of sql header
     *
     * @return sql header
     */
    protected abstract String sqlHeader();

    /**
     * Give an array of serach conditions for the frame to format a where clause.
     *
     * @return search condition string array. eg. {col0 = 10, col1 > 0}
     */
    protected abstract String[] searchConditions();

    /**
     * @return 它应当返回sql语句的整个尾部
     */
    protected String sqlTail() {
        return "";
    }

    private String buildWhere(Map<String, Object> map) {
        String where = " where ";
        for (String whereFragment : searchConditions()) {
            if (!ignoreWhereFragment(whereFragment, map)) {
                where = where.concat(" ").concat(adjustWhereFragment(where, whereFragment));
            }
        }
        if (where.endsWith(" where ")) {
            where = where.substring(0, where.length() - " where ".length());
        }
        return where;
    }

    private String adjustWhereFragment(String former, String fragment) {
        former = former.trim().toLowerCase();
        String lower = fragment.trim().toLowerCase();
        if (!former.endsWith(" and") && !former.endsWith(" or") && !former.endsWith("where")) {
            fragment = lower.startsWith("and ") || lower.startsWith("or ") ? fragment : " and ".concat(fragment);
        }
        PatternUtil.adjustLikeClause(fragment, getMap());
        return fragment;
    }

    final boolean ignoreWhereFragment(String whereFragment, Map<String, Object> map) {
        for (Object value : PatternUtil.getValues(whereFragment, map)) {
            if (value == null) {
                return true;
            }
            // 属性值是一个集合，那么如果集合size为0，那么直接忽略此查询条件
            if (value instanceof Collection && ((Collection) value).isEmpty()) {
                return true;
            }
            if (StringUtil.isEmpty(value)) {
                // 这里认为空串“”也是空，因此使用StringUtil.isEmpty()做判断
                return true;
            }
        }
        return false;
    }
}
