package info.xiancloud.qcloudcos.api;

import com.alibaba.fastjson.JSON;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.util.LOG;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置项
 *
 * @author yyq
 */
public class QCloudCosConfig {

    public final static String SecretId;//"AKIDc3E8mAz8i5EBuQBneHgroX3LQIwM7ZMv",//"AKIDjKg7PVp2SglbFif82h7LM9h04tWvb6sU";// "AKIDJoBNM835GE0Zx9z7rOjNwW0OdhYDsx40";
    public final static String SecretKey; // "RineHgroY5OUM1SfePxMYWGo8E7nGVbU";//"ax53rRlslOn34pQoz1nLgmdLsBFBtNfk";// "tlTzIOK4bw64qQp47yc5MRK1XyH4oYN8";

    // 签名过期时间 单位秒
    private int signTimeOut = 3000;
    // 用来签名的key过期时间 单位秒
    private int signKyeTimeOut = 3000;

    private String urlPre = "http://";

    /**
     * bucket对应的xml api url
     */
    private final static Map<String, String> bucketHost = new HashMap<String, String>();

    static {
        String[] urls = XianConfig.getStringArray("bucketurl");
        if (urls != null && urls.length > 0) {
            for (String url : urls) {
                String[] u = url.split(":");
                bucketHost.put(u[0], u[1]);
            }
        } else {
            LOG.error("qcloud-xml-api:没有配置bucket和host的映射数据.....请重新配置");
        }
        SecretId = XianConfig.get("secretid");
        SecretKey = XianConfig.get("secretkey");

        LOG.info(String.format("bucketurl初始化完成 : [%s]", JSON.toJSONString(bucketHost)));
        LOG.info(String.format("SecretId初始化完成 : %s", SecretId));
        LOG.info(String.format("SecretKey初始化完成 : %s", SecretKey));

    }

    public static QCloudCosConfig build() {
        QCloudCosConfig config = new QCloudCosConfig();
        return config;
    }

    /**
     * 是否是有效的bucket
     *
     * @return
     */
    public static boolean validBucket(String bucketName) {
        return bucketHost.containsKey(bucketName);
    }

    /**
     * 获取bucket对于的host
     *
     * @return
     */
    public static String bucketHost(String bucketName) {
        return bucketHost.get(bucketName);
    }

    public int getSignTimeOut() {
        return signTimeOut;
    }

    public QCloudCosConfig setSignTimeOut(int signTimeOut) {
        this.signTimeOut = signTimeOut;
        return this;
    }

    public int getSignKyeTimeOut() {
        return signKyeTimeOut;
    }

    public QCloudCosConfig setSignKyeTimeOut(int signKyeTimeOut) {
        this.signKyeTimeOut = signKyeTimeOut;
        return this;
    }

    public String getUrlPre() {
        return urlPre;
    }

    public void setUrlPre(String urlPre) {
        this.urlPre = urlPre;
    }
}
