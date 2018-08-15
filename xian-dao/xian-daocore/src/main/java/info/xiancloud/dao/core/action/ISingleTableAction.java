package info.xiancloud.dao.core.action;

import info.xiancloud.dao.core.utils.TableMetaCache;

/**
 * single table sql action
 *
 * @author happyyangyuan
 */
public interface ISingleTableAction {
    /**
     * this method provides the table name.
     * this method is reentrant.
     *
     * @return table name
     */
    String getTableName();

    /**
     * this methods provides all column names of the specified table.
     * Read from {@link TableMetaCache}, and the cache refreshes every certain seconds.
     *
     * @return the column names of the specified table
     */
    default String[] getCols() {
        return TableMetaCache.COLS.getUnchecked(getTableName());
    }

}
