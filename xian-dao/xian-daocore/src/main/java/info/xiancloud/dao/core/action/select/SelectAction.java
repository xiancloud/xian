package info.xiancloud.dao.core.action.select;

import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.action.WhereAction;
import info.xiancloud.dao.core.model.sqlresult.ISelectionResult;
import io.reactivex.Single;

import java.util.Arrays;

/**
 * @author happyyangyuan
 */
public abstract class SelectAction extends WhereAction implements ISelect {

    private Object selectList;
    /**
     * table names string split by comma
     */
    private Object sourceTable;

    @Override
    public final Single<? extends ISelectionResult> executeSql() {
        return SelectActionHelper.executeWhichSql(this, resultType());
    }

    @Override
    public int resultType() {
        return SELECT_LIST;
    }

    @Override
    public final Object getSelectList() {
        if (selectList == null) {
            selectList = selectList();
        }
        return selectList;
    }

    @Override
    public final Object getSourceTable() {
        if (sourceTable == null) {
            sourceTable = sourceTable();
        }
        return sourceTable;
    }

    /**
     * provides select list for the query below:
     * <br><code>
     * SELECT select_list
     * [ FROM table_source ] [ WHERE search_condition ]
     * </code>
     *
     * @return select_list
     */
    protected abstract Object selectList();

    /**
     * provides table source for the query below:
     * <code>
     * SELECT select_list
     * [ FROM table_source ] [ WHERE search_condition ]
     * </code>
     *
     * @return table source is table name list for the select sql, can be either array, list or string split by comma.
     */
    protected abstract Object sourceTable();

    @Override
    protected final String sqlHeader() {
        StringBuilder select = new StringBuilder("select * from ");
        if (getSelectList() != null) {
            if (getSelectList() instanceof String && !StringUtil.isEmpty(getSelectList())) {
                select = new StringBuilder("select").append(getSelectList().toString()).append(" from ");
            }
            if (getSelectList() instanceof String[]) {
                String[] array = (String[]) getSelectList();
                if (array.length > 0) {
                    String arrayStr = Arrays.toString(array);
                    select = new StringBuilder("select ").append(arrayStr.substring(1, arrayStr.length() - 1)).append(" from ");
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        if (sourceTable() instanceof String) {
            sb.append(sourceTable());
        } else {
            for (String table : (String[]) sourceTable()) {
                sb.append(getSplitter(table)).append(table);
            }
            sb = new StringBuilder(sb.substring(1));
        }
        return select.append(sb).toString();
    }

    private String getSplitter(String tableName) {
        //fixme 不支持关键字join前后不是空格而是换行的情形
        if (tableName.toLowerCase().contains(" join ")) {
            return " ";
        } else {
            return ",";
        }
    }

}
