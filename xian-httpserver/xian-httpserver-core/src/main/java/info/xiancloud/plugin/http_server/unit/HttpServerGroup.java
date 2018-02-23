package info.xiancloud.plugin.http_server.unit;

import info.xiancloud.plugin.Group;

/**
 * @author happyyangyuan
 */
public class HttpServerGroup implements Group {

    public static final String CODE_HTTP_SERVER_ERROR = "HTTP_SERVER_ERROR";
    public final static Group singleton = new HttpServerGroup();

    @Override
    public String getName() {
        return "httpServer";
    }

    @Override
    public String getDescription() {
        return "基于netty的http服务";
    }

}
