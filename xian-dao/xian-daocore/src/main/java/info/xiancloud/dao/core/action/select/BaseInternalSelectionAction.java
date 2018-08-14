package info.xiancloud.dao.core.action.select;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.action.AbstractSqlAction;
import info.xiancloud.dao.core.action.WhereAction;
import info.xiancloud.dao.core.model.ddl.Column;
import info.xiancloud.dao.core.model.ddl.Para;
import info.xiancloud.dao.core.model.ddl.Table;
import info.xiancloud.dao.core.model.ddl.TableHeader;
import info.xiancloud.dao.core.model.sqlresult.ISelectionResult;
import info.xiancloud.dao.core.utils.SqlUtils;
import info.xiancloud.dao.jdbc.SqlUtils;
import io.reactivex.Single;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract internal selection action.
 * With help of this internal selection action, you can submit a query request without writing any sql clause.
 * This action can read operators in parameter map and construct a sql all by itself. You don't need to write any sql statement.
 */
public abstract class BaseInternalSelectionAction extends WhereAction implements ISelect {

    @Override
    public int resultType() {
        return SELECT_LIST;
    }

    @Override
    public final Object getSelectList() {
        return null;
    }

    /*
    @Override
    protected String[] where() {
        return new String[]{};
    }*/

    /**
     * @return 应当返回完整的order by子句,或者穿空忽略order by
     */
    protected Object orderBy() {
        return getMap().get("$orderBy");
    }

    /**
     * @return 应当返回完整的group by子句,或者穿空忽略group by
     */
    protected Object groupBy() {
        return getMap().get("$groupBy");
    }

    /**
     * 一堆正则匹配
     */
    final protected static Pattern HAS_GROUP_BY_PATTERN = Pattern.compile(
            "group\\s+by*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    final protected static Pattern HAS_ORDER_BY_PATTERN = Pattern.compile(
            "order\\s+by*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    final protected static Pattern CHECK_ORDER_BY_PATTERN = Pattern.compile(
            "^(.+?)\\s*(asc|desc){0,}$",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    final protected static String ORDER_BY_STR = "order by ";
    final protected static String GROUP_BY_STR = "group by ";

    @Override
    protected String patternSql() {
        return buildSqlPattern();
    }

    /**
     * rebuild the pattern sql
     */
    private String buildSqlPattern() {
        if (where() == null || where().length == 0) {
            ArrayList<Object> paramArrayList = new ArrayList<Object>();
            StringBuilder sb = new StringBuilder();

            Map<String, Object> argMap = map;
            for (Entry<String, Object> entry : argMap.entrySet()) {
                Column column = getColumn(entry.getKey());
                if (column != null) {
                    Para cir = Cnd.rule(column, entry.getValue());
                    Cnd.buildSQL(sb, cir.getType(), column.getName(), cir.getValue(), "", paramArrayList);
                }
            }
            this.preparedSql = sqlHeader(map, connection).concat(" where 1>0 ").concat(sb.toString()).concat(" ").concat(sqlTail(daoUnit, map, connection));
            this.sqlParams = paramArrayList.toArray();
            return this.preparedSql;
        }
        return SqlUtils.mapToSql(adjustInClause(super.patternSql()), getMap());
    }
}
