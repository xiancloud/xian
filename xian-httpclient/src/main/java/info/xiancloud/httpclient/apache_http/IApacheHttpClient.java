package info.xiancloud.httpclient.apache_http;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.Closeable;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * 为了方便起见,对于不同的header请构造不同的IHttpClient实例
 *
 * @author happyyangyuan
 */
public interface IApacheHttpClient extends Closeable {

    String get() throws ConnectTimeoutException, SocketTimeoutException;

    HttpResponse getHttpResponse() throws ConnectTimeoutException, SocketTimeoutException;

    String post(String body) throws ConnectTimeoutException, SocketTimeoutException;

    String delete(String body) throws ConnectTimeoutException, SocketTimeoutException;

    String put(String body) throws ConnectTimeoutException, SocketTimeoutException;

    String getUrl();

    Map<String, String> getHeaders();

}
