package info.xiancloud.plugin.http_server.event;

import java.io.Serializable;

/**
 * http server收到请求事件
 *
 * @author happyyangyuan
 */
public class HttpRequestEvent implements Serializable {
    private String $msgId;
    private String $body;
    private String $url;
    private String ip;
    private String accessToken;

    public String get$msgId() {
        return $msgId;
    }

    public void set$msgId(String $msgId) {
        this.$msgId = $msgId;
    }

    public String get$body() {
        return $body;
    }

    public void set$body(String $body) {
        this.$body = $body;
    }

    public String get$url() {
        return $url;
    }

    public void set$url(String $url) {
        this.$url = $url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
