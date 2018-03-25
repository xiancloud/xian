package info.xiancloud.httpclient;

import info.xiancloud.core.Group;
import info.xiancloud.core.socket.ISocketGroup;

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
