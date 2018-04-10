package info.xiancloud.yy.http;

import info.xiancloud.core.socket.ConnectTimeoutException;
import info.xiancloud.core.util.HttpUtil;
import info.xiancloud.core.util.Xmap;

import java.net.SocketTimeoutException;

/**
 * @author happyyangyuan
 */
public class HttpInterceptorTest {
    public static void main(String[] args) throws SocketTimeoutException, ConnectTimeoutException {
        System.out.println(HttpUtil.get("http://baidu.com", Xmap.create()));
    }
}
