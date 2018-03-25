package info.xiancloud.qcloudcos.api.util;

import info.xiancloud.core.util.LOG;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 签名工具类
 *
 * @author yyq
 */
public class SignUtil {

    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String CHARSET = "UTF-8";

    /**
     * hamc_sha1 签名
     * <p>
     * JAVA对PHP hmac_sha1 编码结果需要注意的地方
     * <p>
     * 1.需要对hmac_sha1编码结果转换为hex格式
     * <p>
     * 2. java中base64的实现和php不一致,其中java并不会在字符串末尾填补=号以把字节数补充为8的整数
     */
    public static String hmac_sha1(String value, String key) {
        try {
            byte[] keyBytes = key.getBytes(CHARSET);
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1);

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(value.getBytes(CHARSET));

            // 编码结果转换为hex格式
            String hexBytes = byte2hex(rawHmac);
            return hexBytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String byte2hex(final byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0xFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs;
    }

    /**
     * sha1加密
     */
    public static String sha1(String str) {
        if (null == str || 0 == str.length()) {
            return null;
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOG.error(e);
        }
        return null;
    }

    /**
     * URL ENCODER 编码
     *
     * @param data data
     * @return encoded url
     */
    public static String urlencoder(String data, String charset) {
        try {
            return URLEncoder.encode(data, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("不支持的编码格式 : " + charset);
        }
    }

    /**
     * URL ENCODER 编码
     *
     * @return encoded url
     */
    public static String urlencoder(String data) {
        return urlencoder(data, CHARSET);
    }

    public static void main(String[] args) throws Exception {

        // 1 计算singKey 指定ngKey的有效时间： 开始时间:截止时间
        String signKey = hmac_sha1("1480932292;1481012292", "AKIDZfbOA78asKUYBcXFrJD0a1ICvR98JM");
        System.out.println("signKey:" + signKey);

        // 2.计算 formatString
        StringBuilder formatString = new StringBuilder();
        formatString.append("get\n"); // 请求方法
        formatString.append("/testfile\n");// 请求URI
        formatString.append("\n"); // 请求参数
        formatString.append("host=testbucket-125000000.cn-north.myqcloud.com&range=bytes%3d0-3\n");// 请求头
        System.out.println("formatString:");
        System.out.println(formatString.toString());
        // 2.1进行sha1加密
        String formarStrngHash = sha1(formatString.toString());
        System.out.println("formatString Hash : " + formarStrngHash);

        // 3.计算StringToSign
        StringBuilder StringToSignBuild = new StringBuilder();
        // 签名算法，默认使用sha1做标识
        StringToSignBuild.append("sha1\n");
        // 签名的有效时间
        StringToSignBuild.append("1480932292;1481012292\n");
        StringToSignBuild.append(formarStrngHash + "\n");
        String StringToSign = StringToSignBuild.toString();
        System.out.println("StringToSign : " + StringToSign);

        // 4.计算签名
        String sign = hmac_sha1(StringToSign, signKey);

        System.out.println("sign : " + sign);
    }

}
