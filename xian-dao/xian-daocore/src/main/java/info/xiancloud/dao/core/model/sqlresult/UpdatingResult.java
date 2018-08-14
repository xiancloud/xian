package info.xiancloud.dao.core.model.sqlresult;

/**
 * result of database Update
 *
 * @author happyyangyuan
 */
public class UpdatingResult implements SqlExecutionResult {

    /**
     * number of updated record
     */
    private int count;

    public int getCount() {
        return count;
    }

    public UpdatingResult setCount(int count) {
        this.count = count;
        return this;
    }
}
