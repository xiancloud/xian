package info.xiancloud.plugin.distribution.service_discovery;

/**
 * unit注册实例id解析和封装器，这是unit服务注册的Id规范
 *
 * @author happyyangyuan
 */
public class UnitInstanceIdBean {
    private String fullName;
    private String nodeId;
    private String unitInstanceId;
    private static final String NODEID_UNIT_DELIMITER = "```";

    public UnitInstanceIdBean(String unitInstanceId) {
        setUnitInstanceId(unitInstanceId);
    }

    public UnitInstanceIdBean(String fullUnitName, String nodeId) {
        setUnitInstanceId(nodeId.concat(NODEID_UNIT_DELIMITER).concat(fullUnitName));
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getUnitInstanceId() {
        return unitInstanceId;
    }

    public void setUnitInstanceId(String unitInstanceId) {
        this.unitInstanceId = unitInstanceId;
        String[] nodeId_unit = unitInstanceId.split(NODEID_UNIT_DELIMITER);
        fullName = nodeId_unit[1];
        nodeId = nodeId_unit[0];
    }
}
