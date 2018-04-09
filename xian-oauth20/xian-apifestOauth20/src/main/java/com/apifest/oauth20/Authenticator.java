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

package com.apifest.oauth20;

import com.apifest.oauth20.api.AuthenticationException;
import com.apifest.oauth20.api.ICustomGrantTypeHandler;
import com.apifest.oauth20.api.IUserAuthentication;
import com.apifest.oauth20.api.UserDetails;
import com.apifest.oauth20.bean.*;
import com.apifest.oauth20.bean.token_request.TokenRequest;
import com.apifest.oauth20.conf.OAuthConfig;
import com.apifest.oauth20.persistence.DBManager;
import com.apifest.oauth20.persistence.DBManagerFactory;
import com.apifest.oauth20.utils.ResponseBuilder;
import com.apifest.oauth20.validator.InputValidator;
import info.xiancloud.core.support.authen.AccessToken;
import info.xiancloud.core.util.LOG;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Main class for authorization.(认证的执行者)
 * todo not fully tested
 *
 * @author Rossitsa Borissova
 */
public class Authenticator {

    static final String BASIC = "Basic ";
    private static final String TOKEN_TYPE_BEARER = "Bearer";
    private static final Pattern CLIENT_CREDENTIALS_PATTERN = Pattern.compile(OAuth20Handler.CLIENT_CREDENTIALS_PATTERN_STRING);
    public static final String SCOPE_SPLITTER = " ";

    protected DBManager db = DBManagerFactory.getInstance();
    protected ScopeService scopeService = new ScopeService();

    /**
     * issue a new clientCredentials
     *
     * @param req http request
     * @return ClientCredentials
     * @throws OAuthException OAuthException
     */
    public Single<ClientCredentials> issueClientCredentials(HttpRequest req) throws OAuthException {
        Single<ClientCredentials> creds;
        String contentType = req.headers().get(HttpHeaderNames.CONTENT_TYPE);

        if (contentType != null && contentType.contains(HttpHeaderValues.APPLICATION_JSON)) {
            ApplicationInfo appInfo;
            FullHttpRequest fullHttpRequest = (FullHttpRequest) req;
            appInfo = InputValidator.validate(fullHttpRequest.content().toString(CharsetUtil.UTF_8), ApplicationInfo.class);
            if (appInfo.valid()) {
                String[] scopeList = appInfo.getScope().split(SCOPE_SPLITTER);
                creds = db.getAllScopes().flatMap(allScopes -> {
                    for (String s : scopeList) {
                        if (matchScope(allScopes, s) == null) {
                            throw new OAuthException(ResponseBuilder.SCOPE_NOT_EXIST, HttpResponseStatus.BAD_REQUEST);
                        }
                    }
                    // check client_id, client_secret passed
                    if ((appInfo.getId() != null && appInfo.getId().length() > 0) && (appInfo.getSecret() != null && appInfo.getSecret().length() > 0)) {
                        // check if passed client_id, client_secret are valid
                        if (areClientCredentialsValid(appInfo.getId(), appInfo.getSecret())) {
                            return db.findClientCredentials(appInfo.getId())
                                    .switchIfEmpty(Single.just(new ClientCredentials(appInfo.getName(), appInfo.getScope(), appInfo.getDescription(), appInfo.getRedirectUri(), appInfo.getId(), appInfo.getSecret(), appInfo.getApplicationDetails())))
                                    .flatMap(clientCredentials -> {
                                        // if a client app with this client_id already registered
                                        return Single.error(new OAuthException(ResponseBuilder.ALREADY_REGISTERED_APP, HttpResponseStatus.BAD_REQUEST));
                                    });
                        } else {
                            throw new OAuthException(ResponseBuilder.INVALID_CLIENT_CREDENTIALS, HttpResponseStatus.BAD_REQUEST);
                        }
                    } else {
                        ClientCredentials clientCredentials = new ClientCredentials(appInfo.getName(), appInfo.getScope(), appInfo.getDescription(), appInfo.getRedirectUri(), appInfo.getApplicationDetails());
                        return Single.just(clientCredentials);
                    }
                }).flatMap(credsTmp -> db
                        .storeClientCredentials(credsTmp)
                        .andThen(Single.just(credsTmp)));
            } else {
                return Single.error(new OAuthException(ResponseBuilder.NAME_OR_SCOPE_OR_URI_IS_NULL, HttpResponseStatus.BAD_REQUEST));
            }
            // return Single.error(new OAuthException(e, ResponseBuilder.CANNOT_REGISTER_APP, HttpResponseStatus.BAD_REQUEST));
        } else {
            return Single.error(new OAuthException(ResponseBuilder.UNSUPPORTED_MEDIA_TYPE, HttpResponseStatus.BAD_REQUEST));
        }
        return creds;
    }

    private Scope matchScope(List<Scope> allScopes, String scopeName) {
        for (Scope scope : allScopes) {
            if (scope.getScope().equals(scopeName)) {
                return scope;
            }
        }
        return null;
    }

    private boolean areClientCredentialsValid(String clientId, String clientSecret) {
        return CLIENT_CREDENTIALS_PATTERN.matcher(clientId).matches() && CLIENT_CREDENTIALS_PATTERN.matcher(clientSecret).matches();
    }

    // /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
    public String blockingIssueAuthorizationCode(HttpRequest req) throws OAuthException {
        AuthRequest authRequest = new AuthRequest(req);
        LOG.info("received client_id:" + authRequest.getClientId());
        if (!isActiveClientId(authRequest.getClientId()).blockingGet()) {
            throw new OAuthException(ResponseBuilder.INVALID_CLIENT_ID, HttpResponseStatus.BAD_REQUEST);
        }
        authRequest.validate();

        String scope = scopeService.getValidScope(authRequest.getScope(), authRequest.getClientId()).blockingGet();
        if (scope == null) {
            throw new OAuthException(ResponseBuilder.SCOPE_NOK_MESSAGE, HttpResponseStatus.BAD_REQUEST);
        }

        AuthCode authCode = new AuthCode(generateCode(), authRequest.getClientId(), authRequest.getRedirectUri(),
                authRequest.getState(), scope, authRequest.getResponseType(), authRequest.getUserId());
        LOG.info("authCode: {" + authCode.getCode() + "}");
        db.storeAuthCode(authCode).blockingAwait();

        // return redirect URI, append param code=[Authcode]
        QueryStringEncoder enc = new QueryStringEncoder(authRequest.getRedirectUri());
        enc.addParam("code", authCode.getCode());
        return enc.toString();
    }

    /**
     * 支持json和form两种表单形式
     * Please make this method asynchronous someday later
     */
    public AccessToken blockingIssueAccessToken(FullHttpRequest req) throws OAuthException {
        TokenRequest tokenRequest = TokenRequest.create(req);
        tokenRequest.validate();
        // check valid client_id, client_secret and status of the client app should be active
        if (!isActiveClient(tokenRequest.getClientId(), tokenRequest.getClientSecret()).blockingGet()) {
            throw new OAuthException(ResponseBuilder.INVALID_CLIENT_CREDENTIALS, HttpResponseStatus.BAD_REQUEST);
        }

        AccessToken accessToken = null;
        if (TokenRequest.AUTHORIZATION_CODE.equals(tokenRequest.getGrantType())) {
            AuthCode authCode = findAuthCode(tokenRequest).blockingGet();
            // TODO: REVISIT: Move client_id check to db query
            if (authCode != null) {
                if (!tokenRequest.getClientId().equals(authCode.getClientId())) {
                    throw new OAuthException(ResponseBuilder.INVALID_CLIENT_ID, HttpResponseStatus.BAD_REQUEST);
                }
                if (authCode.getRedirectUri() != null
                        && !tokenRequest.getRedirectUri().equals(authCode.getRedirectUri())) {
                    throw new OAuthException(ResponseBuilder.INVALID_REDIRECT_URI, HttpResponseStatus.BAD_REQUEST);
                } else {
                    // invalidate the auth code
                    db.updateAuthCodeValidStatus(authCode.getCode(), false).blockingAwait();
                    accessToken = new AccessToken(TOKEN_TYPE_BEARER, getExpiresIn(TokenRequest.PASSWORD, authCode.getScope()),
                            authCode.getScope(), getExpiresIn(TokenRequest.REFRESH_TOKEN, authCode.getScope()));
                    accessToken.setUserId(authCode.getUserId());
                    accessToken.setClientId(authCode.getClientId());
                    accessToken.setCodeId(authCode.getId());
                    db.storeAccessToken(accessToken).blockingAwait();
                }
            } else {
                throw new OAuthException(ResponseBuilder.INVALID_AUTH_CODE, HttpResponseStatus.BAD_REQUEST);
            }
        } else if (TokenRequest.REFRESH_TOKEN.equals(tokenRequest.getGrantType())) {
            accessToken = db.findAccessTokenByRefreshToken(tokenRequest.getRefreshToken(), tokenRequest.getClientId()).blockingGet();
            if (accessToken != null) {
                if (!accessToken.refreshTokenExpired()) {
                    String validScope;
                    if (tokenRequest.getScope() != null) {
                        if (scopeService.scopeAllowed(tokenRequest.getScope(), accessToken.getScope())) {
                            validScope = tokenRequest.getScope();
                        } else {
                            throw new OAuthException(ResponseBuilder.SCOPE_NOK_MESSAGE, HttpResponseStatus.BAD_REQUEST);
                        }
                    } else {
                        validScope = accessToken.getScope();
                    }
                    db.updateAccessTokenValidStatus(accessToken.getToken(), false).blockingAwait();
                    AccessToken newAccessToken = new AccessToken(TOKEN_TYPE_BEARER, getExpiresIn(TokenRequest.PASSWORD,
                            validScope), validScope, accessToken.getRefreshToken(), accessToken.getRefreshExpiresIn());
                    newAccessToken.setUserId(accessToken.getUserId());
                    newAccessToken.setDetails(accessToken.getDetails());
                    newAccessToken.setClientId(accessToken.getClientId());
                    db.storeAccessToken(newAccessToken).blockingAwait();
                    db.removeAccessToken(accessToken.getToken()).blockingAwait();
                    return newAccessToken;
                } else {
                    db.removeAccessToken(accessToken.getToken()).blockingAwait();
                    throw new OAuthException(ResponseBuilder.INVALID_REFRESH_TOKEN, HttpResponseStatus.BAD_REQUEST);
                }
            } else {
                throw new OAuthException(ResponseBuilder.INVALID_REFRESH_TOKEN, HttpResponseStatus.BAD_REQUEST);
            }
        } else if (TokenRequest.CLIENT_CREDENTIALS.equals(tokenRequest.getGrantType())) {
            ClientCredentials clientCredentials = db.findClientCredentials(tokenRequest.getClientId()).blockingGet();
            String scope = scopeService.getValidScopeByScope(tokenRequest.getScope(), clientCredentials.getScope());
            if (scope == null) {
                throw new OAuthException(ResponseBuilder.SCOPE_NOK_MESSAGE, HttpResponseStatus.BAD_REQUEST);
            }

            accessToken = new AccessToken(TOKEN_TYPE_BEARER, getExpiresIn(TokenRequest.CLIENT_CREDENTIALS, scope),
                    scope, false, null);
            accessToken.setClientId(tokenRequest.getClientId());
            Map<String, String> applicationDetails = clientCredentials.getApplicationDetails();
            if ((applicationDetails != null) && (applicationDetails.size() > 0)) {
                accessToken.setDetails(applicationDetails); // For backward compatibility
                accessToken.setApplicationDetails(applicationDetails);
            }
            db.storeAccessToken(accessToken).blockingAwait();
        } else if (TokenRequest.PASSWORD.equals(tokenRequest.getGrantType())) {
            ClientCredentials clientCredentials = db.findClientCredentials(tokenRequest.getClientId()).blockingGet();
            String scope = scopeService.getValidScopeByScope(tokenRequest.getScope(), clientCredentials.getScope());
            if (scope == null) {
                throw new OAuthException(ResponseBuilder.SCOPE_NOK_MESSAGE, HttpResponseStatus.BAD_REQUEST);
            }

            try {
                UserDetails userDetails = authenticateUser(tokenRequest.getUsername(), tokenRequest.getPassword(), req);
                if (userDetails != null && userDetails.getUserId() != null) {
                    accessToken = new AccessToken(TOKEN_TYPE_BEARER, getExpiresIn(TokenRequest.PASSWORD, scope), scope,
                            getExpiresIn(TokenRequest.REFRESH_TOKEN, scope));
                    accessToken.setUserId(userDetails.getUserId());
                    accessToken.setDetails(userDetails.getDetails());
                    accessToken.setClientId(tokenRequest.getClientId());
                    accessToken.setApplicationDetails(clientCredentials.getApplicationDetails());
                    db.storeAccessToken(accessToken).blockingAwait();
                } else {
                    throw new OAuthException(ResponseBuilder.INVALID_USERNAME_PASSWORD, HttpResponseStatus.UNAUTHORIZED);
                }
            } catch (AuthenticationException e) {
                // in case some custom response should be returned other than HTTP 401
                // for instance, if the user authentication requires more user details as a subsequent step
                if (e.getResponse() != null) {
                    String responseContent = ((FullHttpResponse) (e.getResponse())).content().toString(CharsetUtil.UTF_8);
                    throw new OAuthException(e, responseContent, e.getResponse().getStatus());
                } else {
                    LOG.error("Cannot authenticate user", e);
                    throw new OAuthException(e, ResponseBuilder.CANNOT_AUTHENTICATE_USER, HttpResponseStatus.UNAUTHORIZED); // NOSONAR
                }
            }
        } else if (tokenRequest.getGrantType().equals(OAuthConfig.getCustomGrantType())) {
            String scope = scopeService.getValidScope(tokenRequest.getScope(), tokenRequest.getClientId()).blockingGet();
            if (scope == null) {
                throw new OAuthException(ResponseBuilder.SCOPE_NOK_MESSAGE, HttpResponseStatus.BAD_REQUEST);
            }
            try {
                accessToken = new AccessToken(TOKEN_TYPE_BEARER, getExpiresIn(TokenRequest.PASSWORD, scope), scope,
                        getExpiresIn(TokenRequest.REFRESH_TOKEN, scope));
                accessToken.setClientId(tokenRequest.getClientId());
                UserDetails userDetails = callCustomGrantTypeHandler(req);
                if (userDetails != null && userDetails.getUserId() != null) {
                    accessToken.setUserId(userDetails.getUserId());
                    accessToken.setDetails(userDetails.getDetails());
                }
                db.storeAccessToken(accessToken).blockingAwait();
            } catch (AuthenticationException e) {
                LOG.error("Cannot authenticate user", e);
                throw new OAuthException(e, ResponseBuilder.CANNOT_AUTHENTICATE_USER, HttpResponseStatus.UNAUTHORIZED);
            }
        }
        return accessToken;
    }

    /**
     * todo not tested
     *
     * @param username    user name
     * @param password    password
     * @param authRequest auth http request
     * @return user details
     * @throws AuthenticationException authentication exception
     */
    protected UserDetails authenticateUser(String username, String password, HttpRequest authRequest) throws AuthenticationException {
        UserDetails userDetails;
        IUserAuthentication ua;
        if (OAuthConfig.getUserAuthenticationClass() != null) {
            try {
                ua = OAuthConfig.getUserAuthenticationClass().newInstance();
                userDetails = ua.authenticate(username, password, authRequest);
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error("cannot instantiate user authentication class", e);
                throw new AuthenticationException(e.getMessage());
            }
        } else {
            // if no specific UserAuthentication used, always returns customerId - 12345
            userDetails = new UserDetails("12345", null);
        }
        return userDetails;
    }

    protected UserDetails callCustomGrantTypeHandler(HttpRequest authRequest) throws AuthenticationException {
        UserDetails userDetails = null;
        ICustomGrantTypeHandler customHandler;
        if (OAuthConfig.getCustomGrantTypeHandler() != null) {
            try {
                customHandler = OAuthConfig.getCustomGrantTypeHandler().newInstance();
                userDetails = customHandler.execute(authRequest);
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error("cannot instantiate custom grant_type class", e);
                throw new AuthenticationException(e.getMessage());
            }
        }
        return userDetails;
    }

    public static String[] getBasicAuthorizationClientCredentials(HttpRequest req) {
        // extract Basic Authorization header
        String authHeader = req.headers().get(HttpHeaderNames.AUTHORIZATION);
        String[] clientCredentials = new String[2];
        if (authHeader != null && authHeader.contains(BASIC)) {
            String value = authHeader.replace(BASIC, "");
            Base64 decoder = new Base64();
            byte[] decodedBytes = decoder.decode(value);
            String decoded = new String(decodedBytes, Charset.forName("UTF-8"));
            // client_id:client_secret - should be changed by client password
            String[] str = decoded.split(":");
            if (str.length == 2) {
                clientCredentials[0] = str[0];
                clientCredentials[1] = str[1];
            }
        }
        return clientCredentials;
    }

    protected Maybe<AuthCode> findAuthCode(TokenRequest tokenRequest) {
        return db.findAuthCode(tokenRequest.getCode()/*, tokenRequest.getRedirectUri()*/);
    }

    /**
     * validate the given token string
     *
     * @param token the token string
     * @return the valid token or null
     */
    public Maybe<AccessToken> isValidToken(String token) {
        return db.findAccessToken(token)
                .flatMap(accessToken -> {
                    LOG.info("token详情:" + accessToken);
                    if (accessToken != null && accessToken.isValid()) {
                        if (accessToken.tokenExpired()) {
                            LOG.info("accessToken 已过期,client_id= " + accessToken.getClientId());
                            db.updateAccessTokenValidStatus(accessToken.getToken(), false)
                                    .subscribe();
                            return Maybe.empty();
                        }
                        return Maybe.just(accessToken);
                    }
                    return Maybe.empty();
                });
    }

    /**
     * Get the full application info by application id
     *
     * @param clientId the application id
     * @return the application info
     */
    public Maybe<ApplicationInfo> getApplicationInfo(String clientId) {
        return db.findClientCredentials(clientId).map(creds -> {
            ApplicationInfo appInfo = null;
            if (creds != null) {
                appInfo = ApplicationInfo.loadFromClientCredentials(creds);
            }
            return appInfo;
        });
    }

    protected String generateCode() {
        return AuthCode.generate();
    }

    protected Single<Boolean> isActiveClientId(String clientId) {
        return db.findClientCredentials(clientId)
                .map(creds -> creds != null && creds.getStatus() == ClientCredentials.ACTIVE_STATUS)
                .switchIfEmpty(Single.just(false))
                ;
    }

    // check only that LOCAL_NODE_ID and clientSecret are valid, NOT that the status is active
    protected Single<Boolean> isValidClientCredentials(String clientId, String clientSecret) {
        return db.findClientCredentials(clientId)
                .map(creds -> creds.getSecret().equals(clientSecret))
                .switchIfEmpty(Single.just(false))
                ;
    }

    protected Single<Boolean> isActiveClient(String clientId, String clientSecret) {
        return db.findClientCredentials(clientId)
                .map(creds -> creds.getSecret().equals(clientSecret) && creds.getStatus() == ClientCredentials.ACTIVE_STATUS)
                .switchIfEmpty(Single.just(false))
                ;
    }

    protected Single<Boolean> isExistingClient(String clientId) {
        return db.findClientCredentials(clientId)
                .map(creds -> true)
                .switchIfEmpty(Single.just(false))
                ;
    }

    protected String getExpiresIn(String tokenGrantType, String scope) {
        return String.valueOf(scopeService.getExpiresIn(tokenGrantType, scope));
    }

    /**
     * revoke the given token.  revoke = 撤销
     *
     * @param req the requested token to be revoked
     * @return OAuthException if the given client id does not exist.  true if the token exists and has been revoked, false if the token does not exist.
     */
    public Single<Boolean> revokeToken(FullHttpRequest req) throws OAuthException {
        RevokeTokenRequest revokeRequest = new RevokeTokenRequest(req);
        revokeRequest.checkMandatoryParams();
        String clientId = revokeRequest.getClientId();
        // check valid client_id, status does not matter as token of inactive client app could be revoked too
        return isExistingClient(clientId).flatMap(existed -> {
            if (!existed) throw new OAuthException(ResponseBuilder.INVALID_CLIENT_ID, HttpResponseStatus.BAD_REQUEST);
            else {
                String token = revokeRequest.getAccessToken();
                return db.findAccessToken(token)
                        .map(Optional::of)
                        .switchIfEmpty(Single.just(Optional.empty()))
                        .flatMap(accessToken -> {
                            if (accessToken.isPresent()) {
                                if (accessToken.get().tokenExpired()) {
                                    LOG.info(String.format("Access token {%s} has already expired, nothing need to do.", token));
                                    return Single.just(true);
                                }
                                if (clientId.equals(accessToken.get().getClientId())) {
                                    return db.removeAccessToken(accessToken.get().getToken())
                                            .andThen(Single.just(true)
                                                    .doOnSuccess(ignored -> LOG.info(String.format("access token {%s} set status invalid", token))));
                                } else {
                                    LOG.info(String.format("access token {%s} is not obtained for that client {%s}", token, clientId));
                                    return Single.just(false);
                                }
                            } else {
                                LOG.info(String.format("access token {%s} not found", token));
                                return Single.just(false);
                            }
                        })
                        ;
            }
        });
    }

    /**
     * blocking update client app
     *
     * @param req      http request
     * @param clientId client id
     * @return true for success , false on failure
     * @throws OAuthException oauth exception
     */
    public boolean blockingUpdateClientApp(HttpRequest req, String clientId) throws OAuthException {
        String contentType = req.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentType != null && contentType.contains(ResponseBuilder.APPLICATION_JSON)) {
//            String LOCAL_NODE_ID = getBasicAuthorizationClientId(req);
//            if (LOCAL_NODE_ID == null) {
//                throw new OAuthException(ResponseBuilder.INVALID_CLIENT_ID, HttpResponseStatus.BAD_REQUEST);
//            }
            if (!isExistingClient(clientId).blockingGet()) {
                throw new OAuthException(ResponseBuilder.INVALID_CLIENT_ID, HttpResponseStatus.BAD_REQUEST);
            }
            ApplicationInfo appInfo;
            try {
                FullHttpRequest fullHttpRequest = (FullHttpRequest) req;
                appInfo = InputValidator.validate(fullHttpRequest.content().toString(CharsetUtil.UTF_8), ApplicationInfo.class);
                if (appInfo.validForUpdate()) {
                    if (appInfo.getScope() != null) {
                        String[] scopeList = appInfo.getScope().split(" ");
                        for (String s : scopeList) {
                            if (db.findScope(s).blockingGet() == null) {
                                throw new OAuthException(ResponseBuilder.SCOPE_NOT_EXIST, HttpResponseStatus.BAD_REQUEST);
                            }
                        }
                    }
                    db.updateClientCredentials(clientId, appInfo.getScope(), appInfo.getDescription(), appInfo.getStatus(),
                            appInfo.getApplicationDetails()).blockingGet();
                } else {
                    throw new OAuthException(ResponseBuilder.UPDATE_APP_MANDATORY_PARAM_MISSING, HttpResponseStatus.BAD_REQUEST);
                }
            } catch (Throwable e) {
                LOG.error("cannot update client application", e);
                throw new OAuthException(e, ResponseBuilder.CANNOT_UPDATE_APP, HttpResponseStatus.BAD_REQUEST);
            }
        } else {
            throw new OAuthException(ResponseBuilder.UNSUPPORTED_MEDIA_TYPE, HttpResponseStatus.BAD_REQUEST);
        }
        return true;
    }

}
