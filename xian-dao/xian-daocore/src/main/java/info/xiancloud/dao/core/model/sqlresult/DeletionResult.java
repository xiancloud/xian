package info.xiancloud.dao.core.model.sqlresult;

/**
 * result of deletion
 *
 * @author happyyangyuan
 */
public class DeletionResult implements SqlExecutionResult{
    /**
     * count for deleted records
     */
    private int count;

    public int getCount() {
        return count;
    }

    public DeletionResult setCount(int count) {
        this.count = count;
        return this;
    }
}
