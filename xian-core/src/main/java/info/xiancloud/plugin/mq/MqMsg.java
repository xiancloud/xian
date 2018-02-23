package info.xiancloud.plugin.mq;

import java.util.Map;

/**
 * @author happyyangyuan
 */
public class MqMsg {

    private byte[] payload;
    private Map<String, Object> context;

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
