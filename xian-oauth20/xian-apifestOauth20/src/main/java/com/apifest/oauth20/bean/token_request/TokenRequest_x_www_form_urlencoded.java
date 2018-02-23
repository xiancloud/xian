package com.apifest.oauth20.bean.token_request;

import com.apifest.oauth20.Authenticator;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专门处理 contentType = application/x-www-form-urlencoded 的token请求
 *
 * @author happyyangyuan
 */
public class TokenRequest_x_www_form_urlencoded extends TokenRequest {

    public TokenRequest_x_www_form_urlencoded(FullHttpRequest request) {
        if (!request.headers().get(HttpHeaderNames.CONTENT_TYPE).contains(contentType())) {
            throw new RuntimeException(String.format("本类%s只支持对%s类型的请求解码", getClass(), contentType()));
        }
        String content = request.content().toString(CharsetUtil.UTF_8);
        List<NameValuePair> values = URLEncodedUtils.parse(content, CharsetUtil.UTF_8);
        Map<String, String> params = new HashMap<>();
        for (NameValuePair pair : values) {
            params.put(pair.getName(), pair.getValue());
        }
        this.grantType = params.get(GRANT_TYPE);
        this.code = params.get(CODE);
        this.redirectUri = params.get(REDIRECT_URI);
        this.clientId = params.get(CLIENT_ID);
        this.clientSecret = params.get(CLIENT_SECRET);
        if (this.clientId == null && this.clientSecret == null) {
            String[] clientCredentials = Authenticator.getBasicAuthorizationClientCredentials(request);
            this.clientId = clientCredentials[0];
            this.clientSecret = clientCredentials[1];
        }
        this.refreshToken = params.get(REFRESH_TOKEN);
        this.scope = params.get(SCOPE);
        this.username = params.get(USERNAME);
        this.password = params.get(PASSWORD);
    }

    private String contentType() {
        return HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString();
    }
}
