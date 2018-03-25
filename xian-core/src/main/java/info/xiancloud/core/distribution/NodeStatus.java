package info.xiancloud.core.distribution;

import java.util.Set;

/**
 * Node's status bean.
 *
 * @author happyyangyuan
 */
public final class NodeStatus {

    private String nodeId;
    private Set<String> units;//本状态只在广播/服务注册时才会写入，其他mq消息发送时该值为空
    private int queueSize;
    private int activeCount;
    private int cpuCores;
    private String host;//rpc服务地址
    private int port;
    private long initTime;//节点初始化时间

    public String getNodeId() {
        return nodeId;
    }

    public NodeStatus setNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public NodeStatus setActiveCount(int activeCount) {
        this.activeCount = activeCount;
        return this;
    }

    public String getHost() {
        return host;
    }

    public NodeStatus setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public NodeStatus setPort(int port) {
        this.port = port;
        return this;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public NodeStatus setQueueSize(int queueSize) {
        this.queueSize = queueSize;
        return this;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public NodeStatus setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
        return this;
    }

    public long getInitTime() {
        return initTime;
    }

    public NodeStatus setInitTime(long initTime) {
        this.initTime = initTime;
        return this;
    }

    public Set<String> getUnits() {
        return units;
    }

    public NodeStatus setUnits(Set<String> units) {
        this.units = units;
        return this;
    }

    public static NodeStatus create() {
        return new NodeStatus();
    }
}
