package info.xiancloud.plugin.dao.core.group.unit.monitor;

import info.xiancloud.plugin.Bean;

/**
 * @author happyyangyuan
 */
public class DbPoolInfoMonitorBean extends Bean {
    private String title;
    private String datasource;
    private int value;
    private String name;
    private String nodeId;
    /**
     * @deprecated 增加这个tag反而让grafana配置更加麻烦
     */
    private String nodeStartDate;

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

    public String getNodeStartDate() {
        return nodeStartDate;
    }

    public DbPoolInfoMonitorBean setNodeStartDate(String nodeStartDate) {
        this.nodeStartDate = nodeStartDate;
        return this;
    }
}
