package info.xiancloud.httpclient.apache_http.no_auth;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.util.LOG;
import info.xiancloud.httpclient.apache_http.IApacheHttpClient;
import info.xiancloud.httpclient.apache_http.pool.ApacheHttpConnManager;
import info.xiancloud.httpclient.apache_http.pool.ConnKeepAliveStrategy;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

/**
 * 阿帕奇http客户端封装
 *
 * @author happyyangyuan
 */
public class ApacheHttpClient implements IApacheHttpClient {

    private RequestConfig requestConfig;
    private final static String INIT_FAIL = "HttpClient create fail";
    private static final Integer DEFAULT_READ_TIME_OUT_IN_MILLIS = 5000;

    final private Map<String, String> headers;
    final private String url;
    protected HttpClient client;

    public static IApacheHttpClient newInstance(String url, Map<String, String> headers) {
        return new ApacheHttpClient(url, headers);
    }

    public static IApacheHttpClient newInstance(String url, Map<String, String> headers, Integer readTimeoutInMillis) {
        return new ApacheHttpClient(url, headers, readTimeoutInMillis);
    }

    protected ApacheHttpClient(String url, Map<String, String> headers) {
        this(url, headers, DEFAULT_READ_TIME_OUT_IN_MILLIS);
    }

    private ApacheHttpClient(String url, Map<String, String> headers, Integer readTimeoutInMillis) {
        requestConfig = getRequestConfig(
                readTimeoutInMillis == null ? DEFAULT_READ_TIME_OUT_IN_MILLIS : readTimeoutInMillis);
        this.headers = headers;
        this.url = url;
        client = getHttpClient();
    }

    @Override
    public String get() throws ConnectTimeoutException, SocketTimeoutException {
        if (client == null) {
            return INIT_FAIL;
        }
        HttpGet httpGet = new HttpGet(url);
        httpGet.setProtocolVersion(HttpVersion.HTTP_1_1);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        String responseContent;
        try {
            httpGet.setConfig(requestConfig);
            HttpResponse httpResponse = client.execute(httpGet);
            responseContent = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (ConnectTimeoutException | SocketTimeoutException connectOrReadTimeout) {
            throw connectOrReadTimeout;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            httpGet.releaseConnection();
        }
        return responseContent;
    }

    @Override
    public HttpResponse getHttpResponse() throws ConnectTimeoutException, SocketTimeoutException {
        if (client == null) {
            throw new RuntimeException("http客户端未初始化!");
        }
        HttpGet httpGet = new HttpGet(url);
        httpGet.setProtocolVersion(HttpVersion.HTTP_1_1);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            httpGet.setConfig(requestConfig);
            return client.execute(httpGet);
        } catch (ConnectTimeoutException | SocketTimeoutException connectOrReadTimeout) {
            throw connectOrReadTimeout;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String post(String body) throws ConnectTimeoutException, SocketTimeoutException {
        /*
         * LOG.info(String.format(
         * "httpClient获取到的header: %s  ;   httpClient获取到的body:%s ", headers,
         * body));
         */
        if (client == null) {
            return INIT_FAIL;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        String responseContent;
        try {
            StringEntity entity = new StringEntity(body == null ? "" : body, "utf-8");
            entity.setContentEncoding("utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.setConfig(requestConfig);
            HttpResponse httpResponse = client.execute(httpPost);
            responseContent = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
        } catch (ConnectTimeoutException | SocketTimeoutException connectOrReadTimeout) {
            throw connectOrReadTimeout;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            httpPost.releaseConnection();
        }
        return responseContent;
    }

    @Override
    public String delete(String body) throws ConnectTimeoutException, SocketTimeoutException {
        return null;
    }

    @Override
    public String put(String body) throws ConnectTimeoutException, SocketTimeoutException {
        return null;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getUrl() {
        return url;
    }

    /**
     * connectionRequestTimeout 从连接池中取出连接的超时时间 connectTimeout 和服务器建立连接的超时时间
     * socketTimeout 从服务器读取数据的超时时间
     */
    private static RequestConfig getRequestConfig(Integer readTimeoutInMillis) {
        return RequestConfig.custom().setConnectionRequestTimeout(600)
                .setConnectTimeout(XianConfig.getIntValue("apache.httpclient.connectTimeout", 600))
                .setSocketTimeout(readTimeoutInMillis).build();
    }

    private HttpClient getHttpClient() {
        HttpClientBuilder hcBuilder = HttpClientBuilder.create().addInterceptorFirst(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                context.setAttribute("startInNano", System.nanoTime());
            }
        }).addInterceptorFirst(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                LOG.info(new JSONObject() {
                    {
                        put("type", "http");
                        long costInMilli = (System.nanoTime() - (long) context.getAttribute("startInNano")) / 1000000;
                        put("cost", costInMilli);
                        put("url", getUrl());
                        put("remoteHost", new URL(getUrl()).getHost());
                    }
                });
            }
        });
        //是否启用连接池功能
        if (XianConfig.getBoolValue("apache.httpclient.pool.open", false)) {
            LOG.info("启用Http连接池");
            hcBuilder.setConnectionManager(ApacheHttpConnManager.create())
                    .setConnectionManagerShared(true)
                    .setKeepAliveStrategy(ConnKeepAliveStrategy.create(30000));
        }

        return hcBuilder.build();
    }

    @Override
    public void close() throws IOException {
        if (client != null)
            ((CloseableHttpClient) client).close();
        else
            LOG.warn("客户端对象client是null，你关个毛线！");
    }
}
