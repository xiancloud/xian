package info.xiancloud.core.distribution.service_discovery;

/**
 * @param <T> payload
 * @author happyyangyuan
 */
public abstract class Instance<T> {
    private String name;
    private String id;
    private String address;
    private Integer port;
    private Integer sslPort;
    private long registrationTimestamp;
    private boolean enabled;
    private T payload;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return 服务host地址
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getSslPort() {
        return sslPort;
    }

    public void setSslPort(Integer sslPort) {
        this.sslPort = sslPort;
    }

    public long getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(long registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    /**
     * @return 实例所在节点的id
     */
    public abstract String getNodeId();
}
