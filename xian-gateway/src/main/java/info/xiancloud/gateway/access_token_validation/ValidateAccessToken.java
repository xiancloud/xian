package info.xiancloud.gateway.access_token_validation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Constant;
import info.xiancloud.core.Scope;
import info.xiancloud.core.Unit;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.support.authen.AccessToken;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.gateway.controller.URIBean;
import io.reactivex.Single;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 1、validate the access token if the client ip not in the white ip list. The client ip is often the 'x-real-ip'.
 * 2、query for the access token and put it into the original map.
 *
 * @author happyyangyuan
 */
public class ValidateAccessToken {

    public static Single<Boolean> validate(UnitRequest request) {
        if (!isSecure(request.getContext().getUri())) {
            //No secure requirement, then we do not check the access token.
            return Single.just(true);
        }
        return fetchAccessTokenAndReturnScope(request)
                .map(scope -> Scope.validate(scope, request.getContext().getGroup(), request.getContext().getUnit())
                );

    }

    private static boolean isSecure(String uri) {
        if (Arrays.asList(XianConfig.getStringArray("api_gateway_white_uri_list")).contains(uri)) {
            //todo add 'secure' property for rule engine，instead of static config
            //todo please deprecate white uri, kept for compatibility only.
            return false;
        }
        URIBean uriBean = URIBean.create(uri);
        try {
            if (!UnitRouter.SINGLETON.newestDefinition(Unit.fullName(uriBean.getGroup(), uriBean.getUnit())).getMeta().isSecure()) {
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
     * @return the scope of the request or
     * {@link AccessTokenFailure} if no token string is provided.
     */
    private static Single<String> fetchAccessTokenAndReturnScope(UnitRequest request) {
        String ip = request.getContext().getIp();
        if (StringUtil.isEmpty(ip))
            throw new IllegalArgumentException("Client's ip is empty, please check!");
        if (isWhiteIp(ip)) {
            return Single.just(Scope.api_all);
        }
        String accessToken = request.getContext().getHeader() == null ? null :
                request.getContext().getHeader().getOrDefault(Constant.XIAN_REQUEST_TOKEN_HEADER, null);
        if (StringUtil.isEmpty(accessToken)) {
            return Single.error(new AccessTokenFailure(null));
        } else {
            return forToken(accessToken).map(accessTokenObject -> {
                request.getContext().setAccessToken(accessTokenObject);
                return accessTokenObject.getScope();
            });
        }
    }

    private static boolean isWhiteIp(String clientIp) {
        for (String ip : XianConfig.getStringArray("api_gateway_white_ip_list", new String[]{"*.*.*.*", "*:*:*:*:*:*:*:*"})) {
            if (match(ip, clientIp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param tokenString the token string
     * @return the access token or AccessTokenFailure exception if validation failed.
     */
    private static Single<AccessToken> forToken(String tokenString) {
        return SingleRxXian.call("OAuth", "validateAccessToken", new JSONObject() {{
            put("accessToken", tokenString);
        }}).map(o -> {
            if (!o.succeeded()) {
                throw new AccessTokenFailure(tokenString);
            }
            return o.dataToType(AccessToken.class);
        });
    }

    /**
     * @return json object which represents the access token object or an {@link AccessTokenFailure} exception.
     * @deprecated for internal usage, we should not use http api to access oauth interface, instead we use unit invocation.
     * use {@link #forToken(String)} instead.
     */
    private static Single<JSONObject> requestForTokenObject(String accessToken) {
        return SingleRxXian.call("httpClient", "apacheHttpClientGet", new JSONObject() {{
            put("url", getOauth20Url(accessToken));
        }}).map(unitResponse -> {
            JSONObject httpResponseJSON = unitResponse.dataToJson();
            if (httpResponseJSON.getJSONObject("statusLine").getIntValue("statusCode") == 200) {
                String tokenJsonStr = httpResponseJSON.getString("entity");
                return JSON.parseObject(tokenJsonStr);
            } else {
                throw new AccessTokenFailure(accessToken);
            }
        });
    }

    /**
     * @deprecated for internal usage, we should not use http api to access oauth interface, instead we use unit invocation.
     */
    private static String getOauth20Url(String accessToken) {
        return "http://" + XianConfig.get("oauth_server_host") + ":" + XianConfig.get("oauth_server_port") + "/oauth2.0/tokens/validate?token=" + accessToken;
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