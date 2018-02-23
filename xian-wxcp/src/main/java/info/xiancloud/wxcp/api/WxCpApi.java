package info.xiancloud.wxcp.api;

import info.xiancloud.wxcp.bean.msg.WxCpMessage;
import info.xiancloud.wxcp.exception.WxErrorException;
import info.xiancloud.wxcp.httpexecutor.WxReqExecutor;

/**
 * 微信API
 */
public interface WxCpApi {

    /**
     * <pre>
     * 验证推送过来的消息的正确性
     * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=验证消息真实性
     * </pre>
     *
     * @param msgSignature 消息签名
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @param data         微信传输过来的数据，有可能是echoStr，有可能是xml消息
     */
    boolean checkSignature(String msgSignature, String timestamp, String nonce, String data);

    /**
     * <pre>
     *   用在二次验证的时候
     *   企业在员工验证成功后，调用本方法告诉企业号平台该员工关注成功。
     * </pre>
     *
     * @param userId 用户id
     */
    void userAuthenticated(String userId) throws WxErrorException;

    /**
     * 获取access_token, 不强制刷新access_token
     *
     * @throws Exception exception
     * @see #getAccessToken(boolean)
     */
    String getAccessToken() throws WxErrorException, Exception;

    /**
     * <pre>
     * 获取access_token，本方法线程安全
     * 且在多线程同时刷新时只刷新一次，避免超出2000次/日的调用次数上限
     * 另：本service的所有方法都会在access_token过期是调用此方法
     * 程序员在非必要情况下尽量不要主动调用此方法
     * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=获取access_token
     * </pre>
     *
     * @param forceRefresh 强制刷新
     * @throws Exception Exception
     */
    String getAccessToken(boolean forceRefresh) throws WxErrorException, Exception;


    /**
     * <pre>
     * 发送消息
     * 详情请见: http://qydev.weixin.qq.com/wiki/index.php?title=%E5%8F%91%E9%80%81%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E
     * </pre>
     *
     * @param message 要发送的消息对象
     */
    void messageSend(WxCpMessage message) throws WxErrorException;


    /**
     * 当本Service没有实现某个API的时候，可以用这个，针对所有微信API中的GET请求
     *
     * @param url        接口地址
     * @param queryParam 请求参数
     */
    String get(String url, String queryParam) throws WxErrorException;

    /**
     * 当本Service没有实现某个API的时候，可以用这个，针对所有微信API中的POST请求
     *
     * @param url      接口地址
     * @param postData 请求body字符串
     */
    String post(String url, String postData) throws WxErrorException;

    /**
     * <pre>
     * Service没有实现某个API的时候，可以用这个，
     * 比{@link #get}和{@link #post}方法更灵活，可以自己构造RequestExecutor用来处理不同的参数和不同的返回类型。
     * 可以参考，me.chanjar.weixin.common.util.http.MediaUploadRequestExecutor 的实现方法
     * </pre>
     *
     * @param executor 执行器
     * @param uri      请求地址
     * @param data     参数
     * @param <T>      请求值类型
     * @param <E>      返回值类型
     */
    <T, E> T execute(WxReqExecutor<T, E> executor, String uri, E data) throws WxErrorException;

    /**
     * 注入 {@link WxCpConfigStorage} 的实现
     *
     * @param wxConfigProvider 配置对象
     */
    void setWxCpConfigStorage(WxCpConfigStorage wxConfigProvider);

    /**
     * <pre>
     * 设置当微信系统响应系统繁忙时，要等待多少 retrySleepMillis(ms) * 2^(重试次数 - 1) 再发起重试
     * 默认：1000ms
     * </pre>
     *
     * @param retrySleepMillis 重试休息时间
     */
    void setRetrySleepMillis(int retrySleepMillis);

    /**
     * <pre>
     * 设置当微信系统响应系统繁忙时，最大重试次数
     * 默认：5次
     * </pre>
     *
     * @param maxRetryTimes 最大重试次数
     */
    void setMaxRetryTimes(int maxRetryTimes);

}
