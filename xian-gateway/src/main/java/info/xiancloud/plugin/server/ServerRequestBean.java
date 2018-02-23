package info.xiancloud.plugin.server;

import info.xiancloud.plugin.Bean;

import java.util.Map;

/**
 * request bean from the netty http server or some other kind of server, whatever.
 *
 * @author happyyangyuan
 */
public class ServerRequestBean extends Bean {
    private String uri;
    private String body;
    private String ip;
    private Map<String, String> header;
    private String msgId;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}
