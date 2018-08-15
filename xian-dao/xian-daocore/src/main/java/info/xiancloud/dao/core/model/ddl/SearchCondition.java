package info.xiancloud.dao.core.model.ddl;

/**
 * A search condition of sql statement's where clause.
 * A where clause is formed with 1 or more search conditions.
 *
 * @author happyyangyuan
 */
public class SearchCondition {
    private String key;
    private Operator operator;
    private Object value;

    public SearchCondition(String key, Operator operator) {
        this.key = key;
        this.operator = operator;
    }

    public SearchCondition(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public SearchCondition(String key, Operator operator, Object value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Operator getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }
}
