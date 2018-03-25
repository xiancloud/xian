package info.xiancloud.httpserver.core.unit;

import info.xiancloud.core.Group;

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
