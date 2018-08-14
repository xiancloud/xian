package info.xiancloud.dao.core.action.select;

import info.xiancloud.dao.core.action.AbstractSqlAction;
import io.reactivex.Single;

/**
 * Custom select sql action.
 * Subclasses must implement the {@link #getPatternSql()} method.
 *
 * @author happyyangyuan
 */
public abstract class CustomSelectAction extends AbstractSqlAction implements ISelect {

    @Override
    public int resultType() {
        return SELECT_LIST;
    }

    //---------------------------------final-------------------------------------------------//

    @Override
    public final String[] getSelectList() {
        //We can parse the pattern sql and get the select_list
        throw new RuntimeException("Unsupported.");
    }

    @Override
    public final Object getSourceTable() {
        //We can parse the pattern sql to get the source table.
        throw new RuntimeException("Unsupported.");
    }

    @Override
    protected final Single<?> executeSql() {
        return SelectActionHelper.executeWhichSql(this, resultType());
    }

}
