package info.xiancloud.nettyhttpserver.http;

import info.xiancloud.core.conf.XianConfig;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * configuration for netty api gateway http server
 *
 * @author happyyangyuan
 */
public class Config {

    /**
     * Socket connection count upper limit.
     */
    public static Integer getBacklog() {
        return 1024;
    }

    public static Integer getPort() {
        return XianConfig.getIntValue("api_gateway_port", 9123);
    }

    public static Integer getClientMaxBodySize() {
        return 104857600;
    }

    public static String getContentType() {
        return "application/json; charset=UTF-8";
    }

    public static Charset defaultUtf8() {
        return CharsetUtil.UTF_8;
    }
}
