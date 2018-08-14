package info.xiancloud.dao.core.model.sqlresult;

import java.util.Map;

/**
 * single record result.
 *
 * @author happyyangyuan
 */
public class SingleRecordSelectionResult implements ISelectionResult {

    private int count = 0;
    private Map<String, Object> record = null;

    /**
     * the actual count of query results.
     * Usually this is 1, but this can be greater than 1 or be zero.
     *
     * @return the actual count of query results.
     */
    public int getCount() {
        return count;
    }

    public SingleRecordSelectionResult setCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * the single result record.
     * if the actual result contains more than one, then this is the first one.
     *
     * @return the first record of the actual query results.
     */
    public Map<String, Object> getRecord() {
        return record;
    }

    public SingleRecordSelectionResult setRecord(Map<String, Object> record) {
        this.record = record;
        return this;
    }
}
