package info.xiancloud.qcloudcos.api.http;

import info.xiancloud.core.util.LOG;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Qcloud cos http 发送客户端
 *
 * @author yyq
 */
public abstract class QCloudCosHttpClient {

    protected final static OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new LoggingInterceptor()).build();

    /**
     * 发送get请求
     */
    protected abstract Response sendGetRequest(HttpRequest request) throws IOException;

    /**
     * 发送post请求
     */
    protected abstract Response sendPostRequest(HttpRequest request);

    /**
     * 发送put请求
     */
    protected abstract Response sendPutRequest(HttpRequest request) throws IOException;

    /**
     * 发送delete请求
     */
    protected abstract Response sendDeleteRequest(HttpRequest request);

    /**
     * 发送http请求
     *
     * @param httpRequest httpRequest
     * @param retType     暂时只支持 String ,byte[] 两种类型
     */
    @SuppressWarnings("unchecked")
    public <T> T sendHttpRequest(HttpRequest httpRequest, Class<T> retType) {
        try {
            HttpMethod method = httpRequest.getMethod();
            Response response = null;
            if (method == HttpMethod.POST) {
                response = sendPostRequest(httpRequest);
            } else if (method == HttpMethod.GET) {
                response = sendGetRequest(httpRequest);
            } else if (method == HttpMethod.PUT) {
                response = sendPutRequest(httpRequest);
            } else if (method == HttpMethod.DELETE) {
                response = sendDeleteRequest(httpRequest);
            } else {
                throw new IllegalArgumentException(String.format("不支持的请求方法 : %s", method));
            }


            if (retType == String.class) {
                return (T) response.body().string();
            } else if (retType == byte[].class) {
                return (T) response.body().bytes();
            }

        } catch (IOException e) {
            LOG.error("qcloud-xml-api请求发生错误：", e);
            throw new RuntimeException("qcloud-xml-api请求发生错误", e);
        }
        return null;
    }
}

class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        System.out.println(String.format("Sending request %s on %s,%s%n%s", request.url(), request.method(),
                chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        System.out.println(String.format("Received response for %s in %.1fms%n%s", response.request().url(),
                (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}