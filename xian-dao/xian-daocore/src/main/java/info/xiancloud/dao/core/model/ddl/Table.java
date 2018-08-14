package info.xiancloud.dao.core.model.ddl;

import java.util.HashMap;
import java.util.Map;

/**
 * Table bean
 *
 * @author happyyangyuan
 */
public class Table {
    public enum Type {
        /**
         * database table
         */
        table,

        /**
         * database view
         */
        view;
    }

    private Type type;
    private String name;
    private String[] primaryKey;
    private String[] unique = null;
    private Map<String, Class<?>> columnTypeMap = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String[] primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Map<String, Class<?>> getColumnTypeMap() {
        return columnTypeMap;
    }

    public void setColumnTypeMap(Map<String, Class<?>> columnTypeMap) {
        this.columnTypeMap = columnTypeMap;
    }

    public void setColumnType(String columnLabel, Class<?> columnType) {
        columnTypeMap.put(columnLabel, columnType);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String[] getUnique() {
        return unique;
    }

    public void setUnique(String[] unique) {
        this.unique = unique;
    }

    public Class<?> getColumnType(String columnLabel) {
        return columnTypeMap.get(columnLabel);
    }

    public Table(String name, String[] primaryKey, Type type, String[] unique) {
        this.name = name;
        this.primaryKey = primaryKey;
        this.type = type;
        this.unique = unique;
    }

    public Table(String name, String[] primaryKey, Map<String, Class<?>> columnTypeMap) {
        this.name = name;
        this.primaryKey = primaryKey;
        this.columnTypeMap = columnTypeMap;
    }
}