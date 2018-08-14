package info.xiancloud.dao.core.model.sqlresult;

import java.util.List;
import java.util.Map;

/**
 * selection result contains a records list.
 *
 * @author happyyangyuan
 */
public class RecordsListSelectionResult implements ISelectionResult {

    /**
     * result row count
     */
    private int count;

    /**
     * query result, record list
     */
    private List<Map<String, Object>> records;

    public int getCount() {
        return count;
    }

    public RecordsListSelectionResult setCount(int count) {
        this.count = count;
        return this;
    }

    public List<Map<String, Object>> getRecords() {
        return records;
    }

    public RecordsListSelectionResult setRecords(List<Map<String, Object>> records) {
        this.records = records;
        return this;
    }
}
