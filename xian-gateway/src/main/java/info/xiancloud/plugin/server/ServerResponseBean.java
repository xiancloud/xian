package info.xiancloud.plugin.server;

import info.xiancloud.plugin.Bean;

/**
 * api gateway response bean
 *
 * @author happyyangyuan
 */
public class ServerResponseBean extends Bean {

    private String msgId;
    private String responseBody;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
