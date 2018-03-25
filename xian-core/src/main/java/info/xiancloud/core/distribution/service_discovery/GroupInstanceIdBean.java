package info.xiancloud.core.distribution.service_discovery;

public class GroupInstanceIdBean {
    private String group;
    private String nodeId;
    private String groupInstanceId;
    private static final String NODEID_GROUP_DELIMITER = "```";

    public GroupInstanceIdBean(String groupInstanceId) {
        setGroupInstanceId(groupInstanceId);
    }

    public GroupInstanceIdBean(String group, String nodeId) {
        setGroupInstanceId(nodeId.concat(NODEID_GROUP_DELIMITER).concat(group));
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getGroupInstanceId() {
        return groupInstanceId;
    }

    /**
     * 保证数据一致性的setter，因此是final的
     */
    final public void setGroupInstanceId(String groupInstanceId) {
        this.groupInstanceId = groupInstanceId;
        String[] nodeId_unit = groupInstanceId.split(NODEID_GROUP_DELIMITER);
        group = nodeId_unit[1];
        nodeId = nodeId_unit[0];
    }
}
