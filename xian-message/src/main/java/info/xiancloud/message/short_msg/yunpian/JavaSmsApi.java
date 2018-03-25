package info.xiancloud.message.short_msg.yunpian;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.socket.ConnectTimeoutException;
import info.xiancloud.core.util.HttpUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信http接口的java代码调用示例
 * 基于Apache HttpClient 4.3
 *
 * @author songchao
 * @since 2015-04-03
 */

public class JavaSmsApi {

    //查账户信息的http地址
    private static String URI_GET_USER_INFO = "http://yunpian.com/v1/user/get.json";

    //智能匹配模版发送接口的http地址
    private static String URI_SEND_SMS = "http://yunpian.com/v1/sms/send.json";

    //模板发送接口的http地址
    private static String URI_TPL_SEND_SMS = "http://yunpian.com/v1/sms/tpl_send.json";

    //发送语音验证码接口的http地址
    private static String URI_SEND_VOICE = "http://yunpian.com/v1/voice/send.json";

    //编码格式。发送编码格式统一用UTF-8
    private static String ENCODING = "UTF-8";

    public static void main(String[] args) throws IOException, URISyntaxException {

        //修改为您的apikey.apikey可在官网（http://www.yunpian.com)登录后用户中心首页看到
        String apikey = XianConfig.get("xian_yunpian_apikey");

        //修改为您要发送的手机号
        String mobile = "18011856576";

        /**************** 查账户信息调用示例 *****************/
        System.out.println(JavaSmsApi.getUserInfo(apikey));

        /**************** 使用智能匹配模版接口发短信(推荐) *****************/
        //设置您要发送的内容(内容必须和某个模板匹配。以下例子匹配的是系统提供的1号模板）
        String text = "您的验证码是1234";
        //发短信调用示例
        System.out.println(JavaSmsApi.sendSms(apikey, text, mobile));

        /* *//**************** 使用指定模板接口发短信(不推荐，建议使用智能匹配模版接口) *****************//*
    //设置模板ID，如使用1号模板:【#company#】您的验证码是#code#
    long tpl_id = 1;
    //设置对应的模板变量值
    //如果变量名或者变量值中带有#&=%中的任意一个特殊符号，需要先分别进行urlencode编码
    //如code值是#1234#,需作如下编码转换
    String codeValue = URLEncoder.encode("#1234#", ENCODING);
    String tpl_value = "#code#=" + codeValue + "&#company#=云片网";
    //模板发送的调用示例
    System.out.println(JavaSmsApi.tplSendSms(apikey, tpl_id, tpl_value, mobile));

    *//**************** 使用接口发语音验证码 *****************//*
    String code = "1234";
    System.out.println(JavaSmsApi.sendVoice(apikey, mobile ,code));*/
    }

    /**
     * 取账户信息
     *
     * @return json格式字符串
     */

    public static String getUserInfo(String apikey) throws IOException, URISyntaxException {
        Map<String, String> params = new HashMap<>();
        params.put("apikey", apikey);
        return post(URI_GET_USER_INFO, params);
    }

    /**
     * 智能匹配模版接口发短信
     *
     * @param apikey apikey
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */

    public static String sendSms(String apikey, String text, String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }

    /**
     * 通过模板发送短信(不推荐)
     *
     * @param apikey    apikey
     * @param tpl_id    　模板id
     * @param tpl_value 　模板变量值
     * @param mobile    　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */

    public static String tplSendSms(String apikey, long tpl_id, String tpl_value, String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
        return post(URI_TPL_SEND_SMS, params);
    }

    /**
     * 通过接口发送语音验证码
     *
     * @param apikey apikey
     * @param mobile 接收的手机号
     * @param code   验证码
     * @return
     */

    public static String sendVoice(String apikey, String mobile, String code) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("mobile", mobile);
        params.put("code", code);
        return post(URI_SEND_VOICE, params);
    }

    /**
     * @param url       提交的URL
     * @param paramsMap parameter map
     * @return 响应
     */
    public static String post(String url, Map<String, String> paramsMap) {
        String applicationXwwwformUrlEncodedString = HttpUtil.xwwwformEncode(paramsMap);
        try {
            return HttpUtil.post(url, applicationXwwwformUrlEncodedString, new HashMap<String, String>() {{
                put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                put("Accept", "application/json;charset=utf-8");
            }});
        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}