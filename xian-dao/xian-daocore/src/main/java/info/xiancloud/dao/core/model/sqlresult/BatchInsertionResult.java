package info.xiancloud.dao.core.model.sqlresult;

/**
 * Batch insertion result bean.
 * Currently batch insertion does not support returning generated keys.
 *
 * @author happyyangyuan
 */
public class BatchInsertionResult implements SqlExecutionResult {

    /**
     * successfully inserted records count
     */
    private int count;

    public int getCount() {
        return count;
    }

    public BatchInsertionResult setCount(int count) {
        this.count = count;
        return this;
    }
}
