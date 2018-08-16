package info.xiancloud.dao.core.action.insert;

import info.xiancloud.dao.core.action.AbstractSqlAction;
import info.xiancloud.dao.core.action.IDML;
import info.xiancloud.dao.core.action.IUnique;
import info.xiancloud.dao.core.model.sqlresult.SingleInsertionResult;
import info.xiancloud.dao.core.utils.BasicSqlBuilder;
import io.reactivex.Single;

/**
 * Sql insertion  action
 *
 * @author happyyangyuan
 */
public abstract class InsertAction extends AbstractSqlAction implements IUnique, IDML {

    private String tableName;

    @Override
    public String getTableName() {
        if (tableName == null) {
            tableName = tableName();
        }
        return tableName();
    }

    /**
     * produces the table name
     *
     * @return the table name
     */
    abstract protected String tableName();

    @Override
    protected Single<SingleInsertionResult> executeSql() {
        return getSqlDriver().insert(getPatternSql(), getMap());
    }

    @Override
    protected String patternSql() {
        return BasicSqlBuilder.buildInserSql(getTableName(), getCols(), getMap());
    }

}
