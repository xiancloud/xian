package info.xiancloud.plugin.access_token_validation;

import info.xiancloud.plugin.util.StringUtil;

/**
 * @author happyyangyuan
 */
public class AccessTokenFailure extends Exception {
    private Object accessToken;

    public AccessTokenFailure(Object accessToken) {
        super(buildTipMsg(accessToken));
        this.accessToken = accessToken;
    }

    private static String buildTipMsg(Object accessToken) {
        if (StringUtil.isEmpty(accessToken)) {
            return "accessToken不允许为空!";
        } else {
            return "accessToken '" + accessToken + "'错误!";
        }
    }

    public Object getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Object accessToken) {
        this.accessToken = accessToken;
    }
}
