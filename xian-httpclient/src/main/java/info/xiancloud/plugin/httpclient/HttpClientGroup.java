package info.xiancloud.plugin.httpclient;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.socket.ISocketGroup;

/**
 * http client unit group
 *
 * @author happyyangyuan
 */
public class HttpClientGroup implements ISocketGroup {
    public static Group singleton = new HttpClientGroup();

    @Override
    public String getName() {
        return "httpClient";
    }

    @Override
    public String getDescription() {
        return "http客户端服务组";
    }
}
