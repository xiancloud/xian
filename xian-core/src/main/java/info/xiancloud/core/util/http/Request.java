package info.xiancloud.core.util.http;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import io.reactivex.Single;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yyq, happyyangyuan
 * @deprecated Warning, not fully tested.
 */
public class Request implements Serializable {

    enum HttpMethod {
        POST, DELETE, GET, PUT, HEAD;
    }

    protected String charset;// 默认编码
    protected String url; // 请求路径
    protected String method;// 请求方法
    // protected transient SSLSocketFactory sslSocketFactory;
    protected transient HttpURLConnection conn;
    protected String cerBase64;
    protected String storePass;
    protected boolean useSSL;

    protected Header header;
    protected RequestBody body;

    public Request(String url) {
        this.charset = Charset.defaultCharset().name();
        this.url = url;
    }

    /**
     * 添加请求参数
     */
    public Request addParam(String pName, Object pValue) {
        if (body == null)
            body = new RequestBody();
        this.body.addParam(pName, pValue);
        return this;
    }

    /**
     * 添加请求参数集合
     */
    public Request addParams(Map params) {
        params.forEach((pName, pValue) -> {
            addParam((String) pName, pValue);
        });
        return this;
    }

    /**
     * 添加请求消息体
     */
    public Request setContent(String content) {
        if (body == null)
            body = new RequestBody();
        this.body.setContent(content);
        return this;
    }

    /**
     * 添加请求头
     */
    public Request addHeader(String hName, String hValue) {
        if (header == null)
            header = new Header();

        this.header.addHeader(hName, hValue);
        return this;
    }

    /**
     * 设置SSL请求证书信息
     */
    /*
     * public UnitRequest setSSL(byte[] cerByte, String storePass) { InputStream
     * cerIn = new ByteArrayInputStream(cerByte); sslSocketFactory =
     * Https.getSslSocketFactory( (cerByte != null && cerByte.length > 0) ? new
     * ByteArrayInputStream(cerByte) : null, storePass); return this; }
     *
     * public UnitRequest setSSL(String cerBase64, String storePass) { byte[]
     * cerByte=Base64.getDecoder().decode(cerBase64); setSSL(cerByte,
     * storePass); return this; }
     *
     * public UnitRequest setSSL(InputStream cerIn, String storePass) {
     * //sslSocketFactory = Https.getSslSocketFactory(cerIn, storePass); return
     * this; }
     */
    public Request setSSL(InputStream cerIn, String storePass) {
        this.useSSL = true;
        // LOG.info("--启用Https访问---");
        if (cerIn != null) {
            ByteArrayOutputStream bos = null;
            try {
                bos = new ByteArrayOutputStream();
                byte[] buffByte = new byte[1024];
                int len = -1;
                while ((len = cerIn.read(buffByte)) > 0) {
                    bos.write(buffByte, 0, len);
                }
                bos.flush();
                this.cerBase64 = new String(Base64.getEncoder().encode(bos.toByteArray()));
            } catch (Exception e) {
                LOG.error("https访问读取证书文件出错", e);
                throw new RuntimeException("https访问读取证书文件出错");
            } finally {
                if (cerIn != null) {
                    try {
                        cerIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cerIn = null;
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bos = null;
                }
            }
        }
        this.storePass = storePass;
        return this;
    }

    /**
     * 异步调用unit执行
     */
    public Single<String> execute() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();

            String reqBase64 = new String(Base64.getEncoder().encode(bos.toByteArray()));
            Map<String, Object> map = new HashMap<>();
            map.put("req", reqBase64);
            return SingleRxXian.call("httpClient", "http", map)
                    .flatMap(response -> Single.just(response.dataToStr()));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * 本地执行请求--测试使用，生产环境禁用
     */
    public Response executeLocal() throws Exception {

        try {
            conn = this.generateConn();
            if (useSSL) {
                InputStream cerIn = null;
                if (!StringUtil.isEmpty(cerBase64)) {
                    cerIn = new ByteArrayInputStream(Base64.getDecoder().decode(cerBase64));
                }
                HttpsURLConnection httpsConn = ((HttpsURLConnection) conn);
                httpsConn.setSSLSocketFactory(Https.getSslSocketFactory(cerIn, storePass));
                httpsConn.setHostnameVerifier(Https.UnSafeHostnameVerifier);
            }

            // 请求方式
            conn.setRequestMethod(method);
            // 链接超时时间
            conn.setConnectTimeout(5000);
            //响应超时时间
            conn.setReadTimeout(XianConfig.getIntValue("httpclient.read_timeout", 1000 * 10));
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            // 构建请求头
            this.generateHeader();
            this.generateBody();

            conn.connect();

            LOG.info(String.format("--请求路径%s,请求方法%s-", url, method));
            Response response = new Response();
            response.setBody(conn.getInputStream());
            response.setStatus(conn.getResponseCode());
            response.setHeaders(conn.getHeaderFields());
            return response;
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    /**
     * 构建请求链接
     *
     * @return HttpURLConnection
     * @throws IOException IOException
     */
    protected HttpURLConnection generateConn() throws IOException {
        HttpURLConnection conn;
        conn = (HttpURLConnection) new URL(url).openConnection();
        return conn;
    }

    /**
     * 构建请求头
     */
    protected void generateHeader() {
        conn.setRequestProperty("Accept-Charset", charset);
        // text/json
        // application/x-www-form-urlencoded
        // text/plain
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Connection", "Keep-Alive");
        if (header != null)
            for (String key : header.getHeaders().keySet()) {
                conn.setRequestProperty(key, header.getHeaders().get(key));
            }
    }

    /**
     * 构建请求体
     *
     * @throws IOException IOException
     */
    protected void generateBody() throws IOException {
        // FIXME
        if (body != null && body.getContent() != null) {
            // 重置请求头中的Content-Type
            conn.setRequestProperty("Content-Type", "text/plain");
            // LOG.info(String.format("--将content数据[%s]写入outPut中",
            // body.getContent()));
            conn.getOutputStream().write(body.getContent().getBytes());
            conn.getOutputStream().flush();
        }
    }

    public String getUrl() {
        return this.url;
    }

    public String method() {
        return this.method;
    }
}
