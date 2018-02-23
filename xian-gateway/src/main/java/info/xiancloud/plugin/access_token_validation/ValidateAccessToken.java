package info.xiancloud.plugin.access_token_validation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Constant;
import info.xiancloud.plugin.Scope;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.distribution.exception.UnitUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.UnitRouter;
import info.xiancloud.plugin.executor.URIBean;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.support.authen.AccessToken;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 1、validate the access token if the client ip not in the white ip list. The client ip is often the 'x-real-ip'.
 * 2、query for the access token and put it into the original map.
 *
 * @author happyyangyuan
 */
public class ValidateAccessToken {

    public static boolean validate(UnitRequest request) {
        if (!isSecure(request.getContext().getUri())) {
            //No secure requirement, then we do not check the access token.
            return true;
        }
        try {
            String scope = fetchAccessTokenAndReturnScope(request);
            return Scope.validate(scope, request.getContext().getGroup(), request.getContext().getUnit());
        } catch (AccessTokenFailure e) {
            LOG.warn(e);
            return false;
        }
    }

    private static boolean isSecure(String uri) {
        if (Arrays.asList(EnvConfig.getStringArray("api_gateway_white_uri_list")).contains(uri)) {
            //todo add 'secure' property for rule engine，instead of static config
            //todo please deprecate white uri, kept for compatibility only.
            return false;
        }
        URIBean uriBean = URIBean.create(uri);
        try {
            if (!UnitRouter.singleton.newestDefinition(Unit.fullName(uriBean.getGroup(), uriBean.getUnit())).getMeta().isSecure()) {
                return false;
            }
        } catch (UnitUndefinedException ignored) {
        }
        return true;
    }

    /**
     * query for access token info and put it into originalMap
     * and return the scope of current token.
     *
     * @return the scope of the request.
     * @throws AccessTokenFailure no token string is provided.
     */
    private static String fetchAccessTokenAndReturnScope(UnitRequest request) throws AccessTokenFailure/*, UnknownScopeException*/ {
        String ip = request.getContext().getIp();
        if (StringUtil.isEmpty(ip))
            throw new IllegalArgumentException("Client's ip is empty, please check!");
        if (isWhiteIp(ip)) {
            return Scope.api_all;
        }
        String accessToken = request.getContext().getHeader() == null ? null :
                request.getContext().getHeader().getOrDefault(Constant.XIAN_REQUEST_TOKEN_HEADER, null);
        if (StringUtil.isEmpty(accessToken)) {
            throw new AccessTokenFailure(null);
        } else {
            AccessToken accessTokenObject = forToken(accessToken);
            request.getContext().setAccessToken(accessTokenObject);
            return accessTokenObject.getScope();
        }
    }

    private static boolean isWhiteIp(String clientIp) {
        for (String ip : EnvConfig.getStringArray("api_gateway_white_ip_list", new String[]{"*.*.*.*", "*:*:*:*:*:*:*:*"})) {
            if (match(ip, clientIp)) {
                return true;
            }
        }
        return false;
    }

    private static AccessToken forToken(String tokenString) throws AccessTokenFailure {
        UnitResponse o = SyncXian.call("OAuth", "validateAccessToken", new JSONObject() {{
            put("accessToken", tokenString);
        }});
        if (!o.succeeded()) {
            throw new AccessTokenFailure(tokenString);
        }
        return o.dataToType(AccessToken.class);
    }

    /**
     * @deprecated for internal usage, we should not use http api to access oauth interface, instead we use unit invocation.
     * use {@link #forToken(String)} instead.
     */
    private static JSONObject requestForTokenObject(String accessToken) throws AccessTokenFailure {
        JSONObject httpResponseJSON = SyncXian.call("httpClient", "apacheHttpClientGet", new JSONObject() {{
            put("url", getOauth20Url(accessToken));
        }}).dataToJson();
        if (httpResponseJSON.getJSONObject("statusLine").getIntValue("statusCode") == 200) {
            String tokenJsonStr = httpResponseJSON.getString("entity");
            return JSON.parseObject(tokenJsonStr);
        } else {
            throw new AccessTokenFailure(accessToken);
        }
    }

    /**
     * @deprecated for internal usage, we should not use http api to access oauth interface, instead we use unit invocation.
     */
    private static String getOauth20Url(String accessToken) {
        return "http://" + EnvConfig.get("oauth_server_host") + ":" + EnvConfig.get("oauth_server_port") + "/oauth2.0/tokens/validate?token=" + accessToken;
    }

    private static boolean match(String myIpPattern, String realIp) {
        LOG.debug("deal with the regex *");
        myIpPattern = myIpPattern.contains(".") ? toIpv4Reg(myIpPattern) : toIpv6Reg(myIpPattern);
        Pattern pattern = Pattern.compile(myIpPattern);
        return pattern.matcher(realIp).matches();
    }

    //convert ipv4 to regex
    private static String toIpv4Reg(String ip) {
        return ip.replace("*", "[0-9]*").replace(".", "\\" + ".");
    }

    //convert ipv6 to regex
    private static String toIpv6Reg(String ip) {
        return ip.replace("*", "[A-Fa-f0-9]*");
    }

}