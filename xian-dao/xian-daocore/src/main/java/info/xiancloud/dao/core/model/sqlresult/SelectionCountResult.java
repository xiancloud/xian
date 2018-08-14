package info.xiancloud.dao.core.model.sqlresult;

/**
 * this is count function result
 */
public class SelectionCountResult implements ISelectionResult {

    private long count;

    /**
     * @return the actual count.
     */
    public long getCount() {
        return count;
    }

    public SelectionCountResult setCount(long count) {
        this.count = count;
        return this;
    }

}
