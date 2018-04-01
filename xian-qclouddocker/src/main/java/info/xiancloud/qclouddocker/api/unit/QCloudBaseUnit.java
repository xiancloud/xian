package info.xiancloud.qclouddocker.api.unit;

import com.alibaba.fastjson.JSON;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.RandomUtils;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.http.HttpKit;
import info.xiancloud.core.util.http.Request;
import info.xiancloud.qclouddocker.api.QCloudBaseArgs;
import info.xiancloud.qclouddocker.api.QCloudConfig;
import info.xiancloud.qclouddocker.api.service.QcloudContainerGroup;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author yyq, happyyangyuan
 */
public abstract class QCloudBaseUnit implements Unit {

    protected final static String EncCharset = "utf-8";

    // 统一使用POST方式请求
    protected final static String HTTP_METHOD = "GET";

    // 腾讯云API的请求路径固定为/v2/index.php
    protected final static String HHTP_APIURL = "/v2/index.php";

    @Override
    public Group getGroup() {
        return QcloudContainerGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        // 填充请求参数
        TreeMap<String, String> params = fillRequestArgs(msg);
        // 生成签名
        String sign = createSignature(params);
        params.put("Signature", sign);

        LOG.info(String.format("腾讯云API调用,请求参数:%s", JSON.toJSONString(params)));

        try {
            Request request = HttpKit.get("https://" + getAPIHost() + HHTP_APIURL);
            for (Entry<String, String> param : params.entrySet()) {
                request.addParam(param.getKey(), param.getValue());
            }
            request.setSSL(null, null);
            String result = request.executeLocal().string();
            return UnitResponse.createSuccess(convert(result));

        } catch (ConnectException e) {
            LOG.error("调用腾讯云API连接超时", e);
            throw new RuntimeException("调用腾讯云API连接超时");
        } catch (SocketTimeoutException e) {
            LOG.error("调用腾讯云API响应超时", e);
            throw new RuntimeException("调用腾讯云API响应超时");
        } catch (Exception e) {
            LOG.error("调用腾讯云API出错", e);
            throw new RuntimeException("调用腾讯云API出错");
        }
    }

    /**
     * 填充请求参数
     *
     * @return
     */
    protected TreeMap<String, String> fillRequestArgs(UnitRequest msg) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        fillBaseArgs(params);
        fillUnitArgs(msg, params);
        return params;
    }

    /**
     * 填充公共请求参数
     *
     * @param params
     */
    protected void fillBaseArgs(TreeMap<String, String> params) {
        QCloudBaseArgs baseArg = createBaseArg(params);
        params.put("Action", baseArg.getAction());
        params.put("Region", baseArg.getRegion());
        params.put("Timestamp", baseArg.getTimestamp() + "");
        params.put("Nonce", baseArg.getNonce() + "");
        params.put("SignatureMethod", baseArg.getSignatureMethod());
        params.put("SecretId", baseArg.getSecretId());
    }

    /**
     * 填充接口请求参数
     */
    protected void fillUnitArgs(UnitRequest msg, TreeMap<String, String> params) {
        // 添加接口请求参数
        List<Input.Obj> argList = this.getInput() != null ? this.getInput().getList() : null;
        if (argList != null && !argList.isEmpty()) {
            argList.forEach(arg -> {
                if (!StringUtil.isEmpty(msg.getArgMap().get(arg.getName()))) {
                    params.put(arg.getName(), msg.getArgMap().get(arg.getName()) + "");
                }
            });
        }
    }

    /**
     * 创建公共请求参数
     *
     * @param params
     * @return
     */
    protected QCloudBaseArgs createBaseArg(TreeMap<String, String> params) {
        QCloudBaseArgs baseArgs = new QCloudBaseArgs();
        baseArgs.setAction(getAction());
        baseArgs.setRegion(QCloudConfig.Region);
        baseArgs.setTimestamp(System.currentTimeMillis() / 1000L);
        baseArgs.setNonce(Integer.parseInt(RandomUtils.getRandomNumbers(5)));
        baseArgs.setSignatureMethod(QCloudConfig.SignatureMethod);
        baseArgs.setSecretId(QCloudConfig.SecretId);
        return baseArgs;
    }


    /**
     * 构建签名
     *
     * @param params 所有的请求参数（不包含签名sign签名）
     * @return
     */
    protected String createSignature(TreeMap<String, String> params) {
        try {
            // 请求方法 + 请求主机 +请求路径 + ? + 请求字符串
            StringBuilder sb = new StringBuilder();
            sb.append(HTTP_METHOD);
            sb.append(getAPIHost());
            sb.append(HHTP_APIURL);
            sb.append("?");
            // FIXME
            if (params != null && !params.isEmpty()) {
                for (Entry<String, String> param : params.entrySet()) {
                    // 值要求非空的
                    if (!StringUtil.isEmpty(param.getValue())) {
                        sb.append(param.getKey() + "=" + param.getValue() + "&");
                    }
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            LOG.info(String.format("--腾讯云容器接口访问,得到签名原串 : %s", sb.toString()));

            byte[] signByte = hMACSHA1Signature(sb.toString().trim(), QCloudConfig.SecretKey);

            // 加密后进行base64编码
            String signBase64 = Base64.getEncoder().encodeToString(signByte);
            LOG.info(String.format("--腾讯云容器接口访问,得到签名Base64编码 : %s", signBase64));

            // 进行URL编码
            String sign = URLEncoder.encode(signBase64, EncCharset);
            LOG.info(String.format("--腾讯云容器接口访问,得到URL签名串 : %s", sign));
            return signBase64;
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException("腾讯云容器接口访问,生成签名出错");
        }

    }

    /**
     * HmacSHA1 签名
     *
     * @param source
     * @param key
     * @return
     */
    protected static byte[] hMACSHA1Signature(String source, String key) {
        try {
            byte[] data = key.getBytes(EncCharset);
            // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKey secretKey = new SecretKeySpec(data, QCloudConfig.SignatureMethod);
            // 生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance(QCloudConfig.SignatureMethod);
            // 用给定密钥初始化 Mac 对象
            mac.init(secretKey);
            byte[] text = source.getBytes(EncCharset);
            // 完成 Mac 操作
            byte[] digest = mac.doFinal(text);
            // String result = bytesToHexString(digest);
            return digest;

        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException("腾讯云容器接口访问,生成签名出错");
        }

    }

    /**
     * 请求的腾讯云接口
     *
     * @return
     */
    protected abstract String getAction();

    /**
     * 腾讯云接口对应的主机
     *
     * @return
     */
    protected abstract String getAPIHost();

    /**
     * FIXME
     * 含有unicode字符串的json串转为中文
     *
     * @param unicodeStr
     * @return
     */
    protected static String convert(String unicodeStr) {
        char aChar;
        int len = unicodeStr.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = unicodeStr.charAt(x++);
            if (aChar == '\\') {
                aChar = unicodeStr.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = unicodeStr.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
}
