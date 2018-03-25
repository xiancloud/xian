package info.xiancloud.qcloudcos.api.request;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.qcloudcos.api.QCloudCosConfig;
import info.xiancloud.qcloudcos.api.http.HttpMethod;
import info.xiancloud.qcloudcos.api.http.HttpRequest;
import info.xiancloud.qcloudcos.api.util.SignUtil;

import java.util.TreeMap;

/**
 * 基础请求
 *
 * @author yyq
 */
public abstract class BaseRequest {

    // 配置项
    private QCloudCosConfig config;

    // bucket名称
    private String bucketName;
    // cos路径
    private String cosPath;

    protected HttpMethod httpMethod;

    protected String host;

    public BaseRequest(String bucketName, String cosPath, QCloudCosConfig config) {
        this.bucketName = bucketName;
        this.cosPath = cosPath;
        this.config = config;
    }

    /**
     * 默认 Host:BucketName-AppID.Region.myqcloud.com/cosPath
     * <p>
     * 构建请求URL http://+Host + cospath
     *
     * @return url
     */
    public String buildUrl() {
        /*host = String.format("%s-%s.%s.%s", bucketName, config.getAppId(), config.getRegion(), config.getDomain());
		String url = String.format("%s%s%s", config.getPre(), host, cosPath);
		*/
        host = QCloudCosConfig.bucketHost(bucketName);
        String url = String.format("%s%s%s", config.getUrlPre(), host, cosPath);
        LOG.info("url构建完成:" + url);
        return url;
    }

    /**
     * 创建请求
     */
    public abstract HttpRequest buildHttpReqest();

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getCosPath() {
        return cosPath;
    }

    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    public QCloudCosConfig getConfig() {
        return config;
    }

    public void setConfig(QCloudCosConfig config) {
        this.config = config;
    }

    /**
     * FIXME 有点残忍，有空再重构 yyq
     * <p>
     * 构建签名
     *
     * @return
     */
    protected String buildAuthorization() {

        // Authorization 构成字段
        String q_sign_algorithm = "sha1";
        String q_ak = QCloudCosConfig.SecretId;
        String q_sign_time = null;
        String q_key_time = null;
        StringBuilder q_header_list = new StringBuilder();
        StringBuilder q_url_param_list = new StringBuilder();
        String q_signature = null;

        long currentTime = System.currentTimeMillis() / 1000;

        // 1.生成SignKey
        // 1.1.生成q-key-time
        long keyBeginTime = currentTime;
        long keyEndTime = keyBeginTime + config.getSignKyeTimeOut();
        q_key_time = String.format("%s;%s", keyBeginTime, keyEndTime);
        System.out.println("q-key-time:\n" + q_key_time);
        String signKey = SignUtil.hmac_sha1(q_key_time, QCloudCosConfig.SecretKey);
        System.out.println("signKey : \n" + signKey);

        // 2.生成StringFormat
        StringBuilder sfBuild = new StringBuilder();
        // 2.1 HttpMethod
        sfBuild.append(httpMethod.name().toLowerCase() + "\n");// httpMethod.name().toLowerCase()
        // 2.2 URI 不包含参数部分
        if (StringUtil.isEmpty(cosPath)) {
            sfBuild.append("\n");
        } else {
            int index = cosPath.indexOf("?");
            if (index != -1) { // URI包含参数
                String uri = cosPath.substring(0, index);
                sfBuild.append(uri + "\n");
            } else {
                sfBuild.append(cosPath + "\n");
            }
        }
        // 2.3 请求参数 key , value 都是小写,都必须经过URL Encode 多个参数用 & 连接
        TreeMap<String, String> params = signParams();
        if (params != null && !params.isEmpty()) {
            StringBuilder sbParams = new StringBuilder();
            params.forEach((k, v) -> {
                // FIXME
                q_url_param_list.append(k.toLowerCase() + ";");
                String key = SignUtil.urlencoder(k.toLowerCase()).toLowerCase();
                String value = SignUtil.urlencoder(v.toLowerCase()).toLowerCase();
                sbParams.append(String.format("%s=%s&", key, value));
            });
            sbParams.deleteCharAt(sbParams.length() - 1);
            q_url_param_list.deleteCharAt(q_url_param_list.length() - 1);
            sfBuild.append(sbParams.toString() + "\n");
        } else {
            sfBuild.append("\n");
        }
        // 2.4 请求头 header key 小写 , value 经过 URL ENCODER 多个用&连接
        TreeMap<String, String> headers = signHeader();
        if (headers != null && !headers.isEmpty()) {
            StringBuilder sbHeaders = new StringBuilder();
            headers.forEach((k, v) -> {
                // FIXME
                q_header_list.append(k.toLowerCase() + ";");
                String key = k.toLowerCase();
                String value = SignUtil.urlencoder(v.toLowerCase()).toLowerCase();
                sbHeaders.append(String.format("%s=%s&", key, value));
            });
            sbHeaders.deleteCharAt(sbHeaders.length() - 1);
            q_header_list.deleteCharAt(q_header_list.length() - 1);
            sfBuild.append(sbHeaders.toString() + "\n");
        } else {
            sfBuild.append("\n");
        }
        String stringFormat = sfBuild.toString();
        System.out.println("stringForamt:\n" + stringFormat);
        System.out.println("stringFormatHash:\n" + SignUtil.sha1(stringFormat));

        // 3. 生成StringToToeken
        StringBuilder ssBuild = new StringBuilder();
        // 3.1签名算法，默认使用sha1做标识
        ssBuild.append("sha1\n");
        // 3.2签名的有效时间
        long signBeginTime = currentTime;
        long signEndTime = signBeginTime + config.getSignTimeOut();
        q_sign_time = String.format("%s;%s", signBeginTime, signEndTime);
        System.out.println("q-sign-time:\n" + q_sign_time);
        // q_sign_time ="1480932292;1481012292";
        ssBuild.append(q_sign_time + "\n");
        // 3.3 stringFormat的sha1加密值
        ssBuild.append(SignUtil.sha1(stringFormat) + "\n");
        String stringToSign = ssBuild.toString();
        System.out.println("StirngToSign :\n" + stringToSign);

        // 4.生成签名
        q_signature = SignUtil.hmac_sha1(stringToSign, signKey);
        System.out.println("q_signature :\n" + q_signature);

        // end 生成Authorization串
        StringBuilder auth = new StringBuilder();
        auth.append("q-sign-algorithm=" + q_sign_algorithm + "&");
        auth.append("q-ak=" + q_ak + "&");
        auth.append("q-sign-time=" + q_sign_time + "&");
        auth.append("q-key-time=" + q_key_time + "&");
        auth.append("q-header-list=" + q_header_list.toString() + "&");
        auth.append("q-url-param-list=" + q_url_param_list.toString() + "&");
        auth.append("q-signature=" + q_signature);

        System.out.println("Authorization :\n" + auth.toString());

        return auth.toString();
    }

    /**
     * 要参与签名的头部
     *
     * @return
     */
    protected abstract TreeMap<String, String> signHeader();

    /**
     * 要参与签名的参数
     *
     * @return
     */
    protected abstract TreeMap<String, String> signParams();

}
