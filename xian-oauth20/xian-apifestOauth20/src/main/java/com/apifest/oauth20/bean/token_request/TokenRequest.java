/*
 * Copyright 2013-2014, ApiFest project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apifest.oauth20.bean.token_request;

import com.alibaba.fastjson.annotation.JSONField;
import com.apifest.oauth20.bean.OAuthException;
import com.apifest.oauth20.conf.OAuthConfig;
import com.apifest.oauth20.utils.ResponseBuilder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Represents token request.
 *
 * @author Rossitsa Borissova
 */
public abstract class TokenRequest {

    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String PASSWORD = "password";

    protected static final String GRANT_TYPE = "grant_type";
    protected static final String CODE = "code";
    protected static final String REDIRECT_URI = "redirect_uri";
    protected static final String CLIENT_ID = "client_id";
    protected static final String CLIENT_SECRET = "client_secret";
    protected static final String SCOPE = "scope";
    protected static final String USERNAME = "username";

    @JSONField(name = GRANT_TYPE)
    protected String grantType;
    protected String code;
    @JSONField(name = REDIRECT_URI)
    protected String redirectUri;
    @JSONField(name = CLIENT_ID)
    protected String clientId;
    @JSONField(name = CLIENT_SECRET)
    protected String clientSecret;
    @JSONField(name = REFRESH_TOKEN)
    protected String refreshToken;
    protected String scope;
    protected String username;
    protected String password;

    protected String userId;

    /**
     * 工厂方法,支持的contentType有application/x-www-form-urlencoded,application/json(默认)
     */
    public static TokenRequest create(FullHttpRequest httpRequest) {
        if (httpRequest.headers() != null && httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE) != null
                && httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE).contains(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED)) {
            return new TokenRequest_x_www_form_urlencoded(httpRequest);
        } else {
            return TokenRequest_json.create(httpRequest);
        }
    }

    public void validate() throws OAuthException {
        checkMandatoryParams();
        if (!grantType.equals(AUTHORIZATION_CODE) && !grantType.equals(REFRESH_TOKEN)
                && !grantType.equals(CLIENT_CREDENTIALS) && !grantType.equals(PASSWORD)
                && !grantType.equals(OAuthConfig.getCustomGrantType())) {
            throw new OAuthException(ResponseBuilder.GRANT_TYPE_NOT_SUPPORTED, HttpResponseStatus.BAD_REQUEST);
        }
        if (grantType.equals(AUTHORIZATION_CODE)) {
            if (code == null) {
                throw new OAuthException(String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, CODE), HttpResponseStatus.BAD_REQUEST);
            }
            if (redirectUri == null) {
                throw new OAuthException(String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, REDIRECT_URI), HttpResponseStatus.BAD_REQUEST);
            }
        }
        if (grantType.equals(REFRESH_TOKEN) && refreshToken == null) {
            throw new OAuthException(
                    String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, REFRESH_TOKEN),
                    HttpResponseStatus.BAD_REQUEST
            );
        }
        if (grantType.equals(PASSWORD)) {
            if (username == null) {
                throw new OAuthException(String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, USERNAME), HttpResponseStatus.BAD_REQUEST);
            }
            if (password == null) {
                throw new OAuthException(String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, PASSWORD), HttpResponseStatus.BAD_REQUEST);
            }
        }
    }

    protected void checkMandatoryParams() throws OAuthException {
        if (clientId == null || clientId.isEmpty()) {
            throw new OAuthException(String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, CLIENT_ID), HttpResponseStatus.BAD_REQUEST);
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new OAuthException(String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, CLIENT_SECRET), HttpResponseStatus.BAD_REQUEST);
        }
        if (grantType == null || grantType.isEmpty()) {
            throw new OAuthException(String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, GRANT_TYPE), HttpResponseStatus.BAD_REQUEST);
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getCode() {
        return code;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getScope() {
        return scope;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
