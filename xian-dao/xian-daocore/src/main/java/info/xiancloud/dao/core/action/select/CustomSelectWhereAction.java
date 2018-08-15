package info.xiancloud.dao.core.action.select;

import info.xiancloud.dao.core.action.WhereAction;
import info.xiancloud.dao.core.model.sqlresult.ISelectionResult;
import io.reactivex.Single;

/**
 * Custom selection and where search condition action.
 * You need to provide a sql header, sql search conditions and a sql tail in order to form a full sql statement.
 *
 * @author happyyangyuan
 */
public abstract class CustomSelectWhereAction extends WhereAction /*SelectAction*/ implements ISelect {

    /**
     * Header of the sql statement. Here for selection sql, sql header is
     * SELECT and FROM clause.
     * <p>
     * Note that WHERE clause is not part of sql header.
     *
     * @return eg. "SELECT xxx,yyy,zzz FROM table0,table1"
     */
    @Override
    protected abstract String sqlHeader();

    @Override
    public int resultType() {
        return SELECT_LIST;
    }


    //-----------------------concrete final-----------------------------------//

    @Override
    public final Single<? extends ISelectionResult> executeSql() {
        return SelectActionHelper.executeWhichSql(this, resultType());
    }

    @Override
    public final Object getSelectList() {
        //we can parse the sql header and get the select list.
        throw new RuntimeException("Not supported.");
    }

    @Override
    public Object getSourceTable() {
        //we can parse the sql header and get the select list.
        throw new RuntimeException("Not supported");
    }

}
