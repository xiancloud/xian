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
import info.xiancloud.gateway.LogTypeGateway;
import info.xiancloud.gateway.controller.URIBean;
import io.reactivex.Single;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 1、validate the access token if the client ip not in the white ip list. The client ip is often the 'x-real-ip'.
 * 2、query for the access token and put it into the original map.
 *
 * @author happyyangyuan
 */
public class ValidateAccessToken {

    /**
     * @param request the unit request
     * @return true if validation passed false otherwise.
     */
    public static Single<Boolean> validate(UnitRequest request) {
        LOG.debug("ValidateAccessToken");
        if (!isSecure(request.getContext().getUri())) {
            //No secure requirement, then we do not check the access token.
            return Single.just(true);
        }
        return fetchAccessTokenAndReturnScope(request)
                .map(optionalScope ->
                        optionalScope.isPresent() && Scope.validate(optionalScope.get(), request.getContext().getGroup(), request.getContext().getUnit())
                );

    }

    private static boolean isSecure(String uri) {
        if (!StringUtil.isEmpty(uri)) {
            //todo add 'secure' property for rule engine，instead of static config
            //todo please deprecate white uri, and use unit's meta property to determine whether a unit is secure or not. See below. This is kept for compatibility only.
            for (String apiGatewayWhiteUri : XianConfig.getStringArray("api_gateway_white_uri_list")) {
                if (uri.startsWith(apiGatewayWhiteUri)) {
                    return false;
                }
            }
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
     * query for access token info and set it into the request context
     * and return the scope of current token.
     *
     * @return the scope of the request or empty
     * if no token string is provided.
     */
    private static Single<Optional<String>> fetchAccessTokenAndReturnScope(UnitRequest request) {
        LOG.info("fetchAccessTokenAndReturnScope");
        String ip = request.getContext().getIp();
        if (StringUtil.isEmpty(ip)) {
            throw new IllegalArgumentException("Client's ip is empty, please check!");
        }
        if (isWhiteIp(ip)) {
            LOG.info(new JSONObject().fluentPut("type", LogTypeGateway.whiteIp)
                    .fluentPut("description", "request is from white ip " + ip)
                    .fluentPut("ip", ip));
            return Single.just(Optional.of(Scope.api_all));
        }
        String accessToken = request.getContext().getHeader() == null ? null :
                request.getContext().getHeader().getOrDefault(Constant.XIAN_REQUEST_TOKEN_HEADER, null);
        if (StringUtil.isEmpty(accessToken)) {
            return Single.just(Optional.empty());
        } else {
            return forToken(accessToken).map(optionalAccessToken -> {
                if (optionalAccessToken.isPresent()) {
                    request.getContext().setAccessToken(optionalAccessToken.get());
                    return Optional.of(optionalAccessToken.get().getScope());
                } else {
                    return Optional.empty();
                }
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
     * @return the access token or empty if token is wrong or expired.
     */
    private static Single<Optional<AccessToken>> forToken(String tokenString) {
        return SingleRxXian
                .call("OAuth", "validateAccessToken", new JSONObject().fluentPut("access_token", tokenString))
                .map(response -> {
                    if (!response.succeeded()) {
                        //wrong token or expired token
                        return Optional.empty();
                    }
                    return Optional.of(response.dataToType(AccessToken.class));
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