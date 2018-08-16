package info.xiancloud.dao.core.model.monitor;

import info.xiancloud.core.Bean;

/**
 * database pool monitor bean.
 *
 * @author happyyangyuan
 */
public class DbPoolInfoMonitorBean implements Bean {
    private String title;
    private String datasource;
    private int value;
    private String name;
    private String nodeId;

    public String getTitle() {
        return title;
    }

    public DbPoolInfoMonitorBean setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDatasource() {
        return datasource;
    }

    public DbPoolInfoMonitorBean setDatasource(String datasource) {
        this.datasource = datasource;
        return this;
    }

    public String getNodeId() {
        return nodeId;
    }

    public DbPoolInfoMonitorBean setNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public DbPoolInfoMonitorBean setValue(int value) {
        this.value = value;
        return this;
    }

    public DbPoolInfoMonitorBean setName(String name) {
        this.name = name;
        return this;
    }

//    public String getNodeStartDate() {
//        return nodeStartDate;
//    }

//    public DbPoolInfoMonitorBean setNodeStartDate(String nodeStartDate) {
//        this.nodeStartDate = nodeStartDate;
//        return this;
//    }
}
