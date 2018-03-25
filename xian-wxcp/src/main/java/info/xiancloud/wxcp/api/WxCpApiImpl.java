package info.xiancloud.wxcp.api;

import info.xiancloud.core.util.LOG;
import info.xiancloud.wxcp.bean.WxAccessToken;
import info.xiancloud.wxcp.bean.msg.WxCpMessage;
import info.xiancloud.wxcp.exception.WxError;
import info.xiancloud.wxcp.exception.WxErrorException;
import info.xiancloud.wxcp.httpexecutor.SimpleGetExecutor;
import info.xiancloud.wxcp.httpexecutor.SimplePostExecutor;
import info.xiancloud.wxcp.httpexecutor.WxReqExecutor;
import info.xiancloud.wxcp.util.SHA1;

import java.io.File;

/**
 * 微信API
 *
 * @author yyq
 */
public class WxCpApiImpl implements WxCpApi {

    // 全局的是否正在刷新access token的锁
    protected final Object globalAccessTokenRefreshLock = new Object();

    protected WxCpConfigStorage configStorage;

    // 临时文件目录
    protected File tmpDirFile;
    // c重试间隔时间
    private int retrySleepMillis = 1000;
    // 最大重试次数
    private int maxRetryTimes = 5;

    @Override
    public boolean checkSignature(String msgSignature, String timestamp, String nonce, String data) {
        try {
            return SHA1.gen(this.configStorage.getToken(), timestamp, nonce, data).equals(msgSignature);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void userAuthenticated(String userId) throws WxErrorException {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/authsucc?userid=" + userId;
        get(url, null);
    }

    @Override
    public String getAccessToken() throws WxErrorException, Exception {
        return getAccessToken(false);
    }

    @Override
    public String getAccessToken(boolean forceRefresh) throws Exception {
        if (forceRefresh) {
            this.configStorage.expireAccessToken();
        }
        if (this.configStorage.isAccessTokenExpired()) {
            synchronized (this.globalAccessTokenRefreshLock) {
                if (this.configStorage.isAccessTokenExpired()) {
                    String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?" + "&corpid="
                            + this.configStorage.getCorpId() + "&corpsecret=" + this.configStorage.getCorpSecret();
                    try {
                        String responseContent = new SimpleGetExecutor().execute(url, null);
                        WxAccessToken accessToken = WxAccessToken.fromJson(responseContent);
                        this.configStorage.updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
                        LOG.info("企业微信API调用获取到accessToken：" + accessToken.getAccessToken());
                    } catch (WxErrorException e) {
                        LOG.error(String.format("企业微信API调用获取accessToken出错,错误吗 %s 错误信息 %s", e.getError().getErrorCode(),
                                e.getError().getErrorMsg()));
                        throw new RuntimeException("企业微信API调用获取accessToken出错", e);
                    }
                }
            }
        }
        return this.configStorage.getAccessToken();
    }

    @Override
    public void messageSend(WxCpMessage message) throws WxErrorException {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send";
        post(url, message.toJson());
    }

    @Override
    public String get(String url, String queryParam) throws WxErrorException {
        return execute(new SimpleGetExecutor(), url, queryParam);
    }

    @Override
    public String post(String url, String postData) throws WxErrorException {
        return execute(new SimplePostExecutor(), url, postData);
    }

    /**
     * 向微信端发送请求，在这里执行的策略是当发生access_token过期时才去刷新，然后重新执行请求，而不是全局定时请求
     */
    @Override
    public <T, E> T execute(WxReqExecutor<T, E> executor, String uri, E data) throws WxErrorException {
        int retryTimes = 0;
        do {
            try {
                T result = this.executeInternal(executor, uri, data);
                LOG.info("企业微信API返回结果:" + result);
                return result;
            } catch (WxErrorException e) {
                if (retryTimes + 1 > this.maxRetryTimes) {
                    LOG.warn(String.format("重试达到最大次数【%s】", this.maxRetryTimes));
                    // 最后一次重试失败后，直接抛出异常，不再等待
                    throw new RuntimeException("企业微信服务端异常，超出重试次数");
                }

                WxError error = e.getError();
                // 系统繁忙, 则进行重新发送
                if (error.getErrorCode() == -1) {
                    try {
                        LOG.warn(String.format("企业微信系统繁忙,%s ms后重试 %s 次", this.retrySleepMillis, retryTimes + 1));
                        Thread.sleep(this.retrySleepMillis);
                    } catch (InterruptedException e1) {
                        throw new RuntimeException(e1);
                    }
                } else {
                    throw e;
                }
            } catch (Exception e) {
                LOG.error("企业微信发送请求出错", e);
                throw new RuntimeException("企业微信发送请求出错");
            }
        } while (retryTimes++ < this.maxRetryTimes);

        throw new RuntimeException("企业微信服务端异常，超出重试次数");
    }

    protected <T, E> T executeInternal(WxReqExecutor<T, E> executor, String uri, E data)
            throws WxErrorException, Exception {
        if (uri.contains("access_token=")) {
            throw new IllegalArgumentException("uri参数中不允许有access_token: " + uri);
        }
        String accessToken = getAccessToken(false);

        String uriWithAccessToken = uri;
        uriWithAccessToken += uri.indexOf('?') == -1 ? "?access_token=" + accessToken : "&access_token=" + accessToken;

        try {
            return executor.execute(uriWithAccessToken, data);
        } catch (WxErrorException e) {
            WxError error = e.getError();
            /*
             * 发生以下情况时尝试刷新access_token 40001
             * 获取access_token时AppSecret错误，或者access_token无效 42001 access_token超时
             */
            if (error.getErrorCode() == 42001 || error.getErrorCode() == 40001) {
                // 强制设置wxCpConfigStorage它的access token过期了，这样在下一次请求里就会刷新access
                // token
                LOG.warn("企业微信access_token超时或失效，重新发起请求...");
                this.configStorage.expireAccessToken();
                return execute(executor, uri, data);
            }
            throw e;
        }
    }

    @Override
    public void setWxCpConfigStorage(WxCpConfigStorage wxConfigProvider) {
        this.configStorage = wxConfigProvider;
    }

    @Override
    public void setRetrySleepMillis(int retrySleepMillis) {
        this.retrySleepMillis = retrySleepMillis;
    }

    @Override
    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public File getTmpDirFile() {
        return this.tmpDirFile;
    }

    public void setTmpDirFile(File tmpDirFile) {
        this.tmpDirFile = tmpDirFile;
    }

}
