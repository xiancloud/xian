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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.api.ExceptionEventHandler;
import com.apifest.oauth20.api.LifecycleHandler;
import com.apifest.oauth20.bean.ApplicationInfo;
import com.apifest.oauth20.bean.ClientCredentials;
import com.apifest.oauth20.bean.OAuthException;
import com.apifest.oauth20.persistence.DBManagerFactory;
import com.apifest.oauth20.utils.QueryParameter;
import com.apifest.oauth20.utils.ResponseBuilder;
import info.xiancloud.core.apidoc.annotation.DocOAuth20;
import info.xiancloud.core.apidoc.annotation.DocOAuth20Sub;
import info.xiancloud.core.apidoc.annotation.DocOAuth20SubIn;
import info.xiancloud.core.support.authen.AccessToken;
import info.xiancloud.core.util.LOG;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler for requests received on the server.
 *
 * @author Rossitsa Borissova
 */
@DocOAuth20
public class OAuth20Handler extends SimpleChannelInboundHandler {

    /**
     * get方法访问该uri,可以拿到附带有code参数的跳转地址
     */
    private static final String AUTH_CODE_URI = "/oauth2.0/auth-codes";
    /**
     * 获取accessToken、refreshToken
     */
    static final String ACCESS_TOKEN_URI = "/oauth2.0/tokens";
    /**
     * 验证token
     */
    static final String ACCESS_TOKEN_VALIDATE_URI = "/oauth2.0/tokens/validate";
    /**
     * application注册(post),改(put),查(get)
     */
    static final String APPLICATION_URI = "/oauth2.0/applications";
    /**
     * 撤销token
     */
    private static final String ACCESS_TOKEN_REVOKE_URI = "/oauth2.0/tokens/revoke";
    /**
     * 授权范围,增(post),删(delete),改(put),查(get)
     */
    static final String OAUTH_CLIENT_SCOPE_URI = "/oauth2.0/scopes";

    static final String CLIENT_CREDENTIALS_PATTERN_STRING = "[a-f[0-9]]+";
    private static final Pattern APPLICATION_PATTERN = Pattern
            .compile("/oauth2\\.0/applications/(" + CLIENT_CREDENTIALS_PATTERN_STRING + ")$");
    private static final Pattern OAUTH_CLIENT_SCOPE_PATTERN = Pattern
            .compile("/oauth2\\.0/scopes/((\\p{Alnum}+-?_?)+$)");

    Authenticator auth = new Authenticator();

    //@DocOAuth20Sub(name = "handle", dec = "Oauth2.0请求统一处理方法，负责分发请求给子方法以及生成HttpResponse", method = "", url = "", args = {
    //	@DocOAuth20SubIn(name = "req", dec = "HTTP请求封装对象", require = true, type = FullHttpRequest.class) })
    public FullHttpResponse handle(FullHttpRequest req) {
        invokeRequestEventHandlers(req);

        HttpMethod method = req.method();
        String rawUri = req.uri();
        try {
            URI u = new URI(rawUri);
            rawUri = u.getRawPath();
        } catch (URISyntaxException e2) {
            LOG.error(String.format("URI syntax exception %s", rawUri));
            invokeExceptionHandler(e2, req);
        }

        FullHttpResponse response;
        if (APPLICATION_URI.equals(rawUri) && method.equals(HttpMethod.POST)) {
            response = handleRegister(req);
        } else if (AUTH_CODE_URI.equals(rawUri) && method.equals(HttpMethod.GET)) {
            response = handleAuthorize(req);
        } else if (ACCESS_TOKEN_URI.equals(rawUri) && method.equals(HttpMethod.POST)) {
            response = handlePostAccessToken(req);
        } else if (ACCESS_TOKEN_VALIDATE_URI.equals(rawUri) && method.equals(HttpMethod.GET)) {
            response = handleTokenValidate(req);
        } else if (APPLICATION_URI.equals(rawUri) && method.equals(HttpMethod.GET)) {
            response = handleGetAllClientApplications(req);
        } else if (rawUri.startsWith(APPLICATION_URI) && method.equals(HttpMethod.GET)) {
            response = handleGetClientApplication(req);
        } else if (ACCESS_TOKEN_REVOKE_URI.equals(rawUri) && method.equals(HttpMethod.POST)) {
            response = handleTokenRevoke(req);
        } else if (OAUTH_CLIENT_SCOPE_URI.equals(rawUri) && method.equals(HttpMethod.GET)) {
            response = handleGetAllScopes(req);
        } else if (OAUTH_CLIENT_SCOPE_URI.equals(rawUri) && method.equals(HttpMethod.POST)) {
            response = handleRegisterScope(req);
        } else if (ACCESS_TOKEN_URI.equals(rawUri) && method.equals(HttpMethod.GET)) {
            response = handleGetAccessTokens(req);
        } else if (rawUri.startsWith(OAUTH_CLIENT_SCOPE_URI) && method.equals(HttpMethod.PUT)) {
            response = handleUpdateScope(req);
        } else if (rawUri.startsWith(OAUTH_CLIENT_SCOPE_URI) && method.equals(HttpMethod.GET)) {
            response = handleGetScope(req);
        } else if (rawUri.startsWith(APPLICATION_URI) && method.equals(HttpMethod.PUT)) {
            response = handleUpdateClientApplication(req);
        } else if (rawUri.startsWith(OAUTH_CLIENT_SCOPE_URI) && method.equals(HttpMethod.DELETE)) {
            response = handleDeleteScope(req);
        } else {
            response = ResponseBuilder.createNotFoundResponse();
        }
        invokeResponseEventHandlers(req, response);
        return response;
    }

    @DocOAuth20Sub(name = "handleGetClientApplication", dec = "获取单个application相关信息", method = "GET", url = "/oauth2.0/applications/{LOCAL_NODE_ID}", args = {
            @DocOAuth20SubIn(name = "client_id", dec = "client_id", require = true, type = String.class)})
    FullHttpResponse handleGetClientApplication(FullHttpRequest req) {
        FullHttpResponse response;
        Matcher m = APPLICATION_PATTERN.matcher(req.uri());
        if (m.find()) {
            String clientId = m.group(1);
            ApplicationInfo appInfo = auth.getApplicationInfo(clientId);
            if (appInfo != null) {
                String json = JSON.toJSONString(appInfo);
                LOG.debug(json);
                response = ResponseBuilder.createOkResponse(json);
            } else {
                response = ResponseBuilder.createResponse(HttpResponseStatus.NOT_FOUND,
                        ResponseBuilder.CLIENT_APP_NOT_EXIST);
            }
        } else {
            response = ResponseBuilder.createNotFoundResponse();
        }
        return response;
    }

    @DocOAuth20Sub(name = "handleTokenValidate", dec = "验证access_token是否有效", method = "GET", url = "/oauth2.0/tokens/validate", args = {
            @DocOAuth20SubIn(name = "access_token", dec = "access_token", require = true, type = String.class)})
    FullHttpResponse handleTokenValidate(FullHttpRequest req) {
        FullHttpResponse response;
        QueryStringDecoder dec = new QueryStringDecoder(req.uri());
        Map<String, List<String>> params = dec.parameters();
        String tokenParam = QueryParameter.getFirstElement(params, QueryParameter.TOKEN);
        if (tokenParam == null || tokenParam.isEmpty()) {
            response = ResponseBuilder.createBadRequestResponse();
        } else {
            AccessToken token = auth.isValidToken(tokenParam);
            if (token != null) {
                String json = JSON.toJSONString(token);
                LOG.debug(json);
                response = ResponseBuilder.createOkResponse(json);
            } else {
                response = ResponseBuilder.createUnauthorizedResponse();
            }
        }
        return response;
    }

    @DocOAuth20Sub(name = "handlePostAccessToken", dec = "获取新access_token", method = "POST", url = "/oauth2.0/tokens", args = {
            @DocOAuth20SubIn(name = "grant_type", dec = "grant_type有四种类型，分别为authorization_code，refresh_token，client_credentials，password", require = true, type = String.class),
            @DocOAuth20SubIn(name = "client_id", dec = "client_id", require = true, type = String.class),
            @DocOAuth20SubIn(name = "client_secret", dec = "client_secret", require = true, type = String.class),
            @DocOAuth20SubIn(name = "redirect_uri", dec = "仅当grant_type为authorization_code时必填", require = false, type = String.class),
            @DocOAuth20SubIn(name = "code", dec = "仅当grant_type为authorization_code时必填", require = false, type = String.class),
            @DocOAuth20SubIn(name = "refresh_token", dec = "仅当grant_type为refresh_token时必填", require = false, type = String.class),
            @DocOAuth20SubIn(name = "scope", dec = "仅当grant_type为refresh_token,client_credentials时填写有效", require = false, type = String.class),
            @DocOAuth20SubIn(name = "username", dec = "仅当grant_type为password时必填", require = false, type = String.class),
            @DocOAuth20SubIn(name = "password", dec = "仅当grant_type为password时必填", require = false, type = String.class),
    })
    FullHttpResponse handlePostAccessToken(FullHttpRequest request) {
        FullHttpResponse response = null;
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentType != null && (contentType.contains(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED)
                || contentType.contains(HttpHeaderValues.APPLICATION_JSON))) {
            try {
                AccessToken accessToken = auth.issueAccessToken(request);
                if (accessToken != null) {
                    String jsonString = JSON.toJSONString(accessToken);
                    LOG.debug("access token:" + jsonString);
                    response = ResponseBuilder.createOkResponse(jsonString);
                    /*accessTokensLog.debug(String.format("token {%s}", jsonString));*/
                }
            } catch (OAuthException ex) {
                response = ResponseBuilder.createOAuthExceptionResponse(ex);
                invokeExceptionHandler(ex, request);
            }
            if (response == null) {
                response = ResponseBuilder.createBadRequestResponse(ResponseBuilder.CANNOT_ISSUE_TOKEN);
            }
        } else {
            response = ResponseBuilder.createResponse(HttpResponseStatus.BAD_REQUEST,
                    ResponseBuilder.UNSUPPORTED_MEDIA_TYPE);
        }
        return response;
    }

    @DocOAuth20Sub(name = "invokeRequestEventHandlers", dec = "触发请求事件监听器的回调方法", method = "", url = "", args = {
            @DocOAuth20SubIn(name = "req", dec = "HTTP请求封装对象", require = true, type = FullHttpRequest.class)})
    private void invokeRequestEventHandlers(FullHttpRequest request) {
        invokeHandlers(request, null, LifecycleEventHandlers.requestEventHandlers);
    }

    @DocOAuth20Sub(name = "invokeResponseEventHandlers", dec = "触发响应事件监听器的回调方法", method = "", url = "", args = {
            @DocOAuth20SubIn(name = "req", dec = "HTTP请求封装对象", require = true, type = FullHttpRequest.class)})
    private void invokeResponseEventHandlers(FullHttpRequest request, FullHttpResponse response) {
        invokeHandlers(request, response, LifecycleEventHandlers.responseEventHandlers);
    }

    @DocOAuth20Sub(name = "invokeExceptionHandler", dec = "触发异常事件监听器的回调方法", method = "", url = "", args = {
            @DocOAuth20SubIn(name = "req", dec = "HTTP请求封装对象", require = true, type = FullHttpRequest.class)})
    private void invokeExceptionHandler(Exception ex, FullHttpRequest request) {
        List<Class<? extends ExceptionEventHandler>> handlers = LifecycleEventHandlers.exceptionHandlers;
        for (int i = 0; i < handlers.size(); i++) {
            try {
                ExceptionEventHandler handler = handlers.get(i).newInstance();
                handler.handleException(ex, request);
            } catch (InstantiationException e) {
                throw new RuntimeException("cannot instantiate exception handler", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("cannot invoke exception handler", e);
            }
        }
    }

    private void invokeHandlers(FullHttpRequest request, FullHttpResponse response,
                                List<Class<? extends LifecycleHandler>> handlers) {
        for (Class<? extends LifecycleHandler> handler1 : handlers) {
            try {
                LifecycleHandler handler = handler1.newInstance();
                handler.handle(request, response);
            } catch (InstantiationException e) {
                LOG.error("cannot instantiate handler", e);
                invokeExceptionHandler(e, request);
            } catch (IllegalAccessException e) {
                LOG.error("cannot invoke handler", e);
                invokeExceptionHandler(e, request);
            }
        }
    }

    @DocOAuth20Sub(name = "handleAuthorize", dec = "获取code", method = "GET", url = "/oauth2.0/auth-codes", args = {
            @DocOAuth20SubIn(name = "response_type", dec = "response_type仅支持code类型", require = true, type = String.class),
            @DocOAuth20SubIn(name = "client_id", dec = "client_id", require = true, type = String.class),
            @DocOAuth20SubIn(name = "state", dec = "state为用户自定义内容，重定向时会带上该参数", require = false, type = String.class),
            @DocOAuth20SubIn(name = "redirect_uri", dec = "redirect_uri", require = true, type = String.class),
            @DocOAuth20SubIn(name = "user_id", dec = "用户自定义值", require = false, type = String.class),
            @DocOAuth20SubIn(name = "scope", dec = "支持由空格分割的多个scope", require = true, type = String.class)
    })
    private FullHttpResponse handleAuthorize(FullHttpRequest req) {
        FullHttpResponse response;
        try {
            String redirectURI = auth.issueAuthorizationCode(req);
            // TODO: validation http protocol?
            LOG.info(String.format("redirectURI: %s", redirectURI));

            // return auth_code
            response = ResponseBuilder.createOkResponse(new JSONObject() {
                {
                    put("redirect_uri", redirectURI);
                }
            }.toString());
            /*accessTokensLog.info("authCode " + response.content().toString(CharsetUtil.UTF_8));*/
        } catch (OAuthException ex) {
            response = ResponseBuilder.createOAuthExceptionResponse(ex);
            invokeExceptionHandler(ex, req);
        }
        return response;
    }

    @DocOAuth20Sub(name = "handleRegister", dec = "注册application", method = "POST", url = "/oauth2.0/applications", args = {
            @DocOAuth20SubIn(name = "name", dec = "application名称", require = true, type = String.class),
            @DocOAuth20SubIn(name = "scope", dec = "支持由空格分割的多个scope", require = true, type = String.class),
            @DocOAuth20SubIn(name = "redirect_uri", dec = "redirect_uri", require = true, type = String.class),
            @DocOAuth20SubIn(name = "client_id", dec = "client_id", require = false, type = String.class),
            @DocOAuth20SubIn(name = "client_secret", dec = "client_secret", require = false, type = String.class),
            @DocOAuth20SubIn(name = "description", dec = "用户自定义application描述", require = false, type = String.class),
            @DocOAuth20SubIn(name = "application_details", dec = "用户自定义的多个键值对", require = false, type = Map.class)
    })
    FullHttpResponse handleRegister(FullHttpRequest req) {
        FullHttpResponse response = null;
        try {
            ClientCredentials creds = auth.issueClientCredentials(req);
            String jsonString = JSON.toJSONString(creds);
            LOG.info("credentials:" + jsonString);
            response = ResponseBuilder.createOkResponse(jsonString);
        } catch (OAuthException ex) {
            response = ResponseBuilder.createOAuthExceptionResponse(ex);
            invokeExceptionHandler(ex, req);
        } catch (Exception e1) {
            LOG.error("error handle register", e1);
            invokeExceptionHandler(e1, req);
        }
        if (response == null) {
            LOG.warn("response is null !", new Throwable());
            response = ResponseBuilder.createBadRequestResponse(ResponseBuilder.CANNOT_REGISTER_APP);
        }
        return response;
    }

    @DocOAuth20Sub(name = "handleTokenRevoke", dec = "撤销已经获取的access_token", method = "POST", url = "/oauth2.0/tokens/revoke", args = {
            @DocOAuth20SubIn(name = "access_token", dec = "access_token", require = true, type = String.class),
            @DocOAuth20SubIn(name = "client_id", dec = "client_id", require = true, type = String.class)
    })
    FullHttpResponse handleTokenRevoke(FullHttpRequest req) {
        boolean revoked = false;
        try {
            revoked = auth.revokeToken(req);
        } catch (OAuthException e) {
            LOG.error("cannot revoke token", e);
            invokeExceptionHandler(e, req);
            return ResponseBuilder.createOAuthExceptionResponse(e);
        }
        String json = "{\"revoked\":\"" + revoked + "\"}";
        return ResponseBuilder.createOkResponse(json);
    }

    @DocOAuth20Sub(name = "handleRegisterScope", dec = "添加新scope", method = "POST", url = "/oauth2.0/scopes", args = {
            @DocOAuth20SubIn(name = "scope", dec = "一次仅能添加一个scope", require = true, type = String.class),
            @DocOAuth20SubIn(name = "description", dec = "自定义scope描述", require = true, type = String.class),
            @DocOAuth20SubIn(name = "cc_expires_in", dec = "grant_type为client_credentials时access_token过期时间", require = true, type = Integer.class),
            @DocOAuth20SubIn(name = "pass_expires_in", dec = "grant_type为password时access_token过期时间", require = true, type = Integer.class),
            @DocOAuth20SubIn(name = "refreshExpiresIn", dec = "grant_type为refresh_token时access_token过期时间，如果不填写，则使用pass_expires_in的值", require = false, type = Integer.class)
    })
    FullHttpResponse handleRegisterScope(FullHttpRequest req) {
        ScopeService scopeService = getScopeService();
        FullHttpResponse response;
        try {
            String responseMsg = scopeService.registerScope(req);
            response = ResponseBuilder.createOkResponse(responseMsg);
        } catch (OAuthException e) {
            invokeExceptionHandler(e, req);
            response = ResponseBuilder.createResponse(e.getHttpStatus(), e.getMessage());
        }
        return response;
    }

    @DocOAuth20Sub(name = "handleUpdateScope", dec = "更新已存在的scope", method = "PUT", url = "/oauth2.0/scopes/{scopeName}", args = {
            @DocOAuth20SubIn(name = "scope", dec = "一次仅能更新一个scope", require = true, type = String.class),
            @DocOAuth20SubIn(name = "description", dec = "自定义scope描述", require = true, type = String.class),
            @DocOAuth20SubIn(name = "cc_expires_in", dec = "grant_type为client_credentials时access_token过期时间", require = true, type = Integer.class),
            @DocOAuth20SubIn(name = "pass_expires_in", dec = "grant_type为password时access_token过期时间", require = true, type = Integer.class),
            @DocOAuth20SubIn(name = "refreshExpiresIn", dec = "grant_type为refresh_token时access_token过期时间", require = true, type = Integer.class)
    })
    FullHttpResponse handleUpdateScope(FullHttpRequest req) {
        FullHttpResponse response;
        Matcher m = OAUTH_CLIENT_SCOPE_PATTERN.matcher(req.uri());
        if (m.find()) {
            String scopeName = m.group(1);
            ScopeService scopeService = getScopeService();
            try {
                String responseMsg = scopeService.updateScope(req, scopeName);
                response = ResponseBuilder.createOkResponse(responseMsg);
            } catch (OAuthException e) {
                invokeExceptionHandler(e, req);
                response = ResponseBuilder.createResponse(e.getHttpStatus(), e.getMessage());
            }
        } else {
            response = ResponseBuilder.createNotFoundResponse();
        }
        return response;
    }

    @DocOAuth20Sub(name = "handleGetAllScopes", dec = "获取所有scope", method = "GET", url = "/oauth2.0/scopes", args = {})
    FullHttpResponse handleGetAllScopes(FullHttpRequest req) {
        ScopeService scopeService = getScopeService();
        FullHttpResponse response;
        try {
            String jsonString = scopeService.getScopes(req);
            response = ResponseBuilder.createOkResponse(jsonString);
        } catch (OAuthException e) {
            invokeExceptionHandler(e, req);
            response = ResponseBuilder.createResponse(e.getHttpStatus(), e.getMessage());
        }
        return response;
    }

    @DocOAuth20Sub(name = "handleGetScope", dec = "获取单个scope", method = "GET", url = "/oauth2.0/scopes/{scopeName}", args = {
            @DocOAuth20SubIn(name = "scope", dec = "scope name", require = true, type = String.class)})
    private FullHttpResponse handleGetScope(FullHttpRequest req) {
        FullHttpResponse response;
        Matcher m = OAUTH_CLIENT_SCOPE_PATTERN.matcher(req.uri());
        if (m.find()) {
            String scopeName = m.group(1);
            ScopeService scopeService = getScopeService();
            try {
                String responseMsg = scopeService.getScopeByName(scopeName);
                response = ResponseBuilder.createOkResponse(responseMsg);
            } catch (OAuthException e) {
                invokeExceptionHandler(e, req);
                response = ResponseBuilder.createResponse(e.getHttpStatus(), e.getMessage());
            }
        } else {
            response = ResponseBuilder.createNotFoundResponse();
        }
        return response;
    }

    @DocOAuth20Sub(name = "handleDeleteScope", dec = "删除单个scope", method = "DELETE", url = "/oauth2.0/scopes/{scopeName}", args = {
            @DocOAuth20SubIn(name = "scope", dec = "scope name", require = true, type = String.class)})
    FullHttpResponse handleDeleteScope(FullHttpRequest req) {
        FullHttpResponse response;
        Matcher m = OAUTH_CLIENT_SCOPE_PATTERN.matcher(req.uri());
        if (m.find()) {
            String scopeName = m.group(1);
            ScopeService scopeService = getScopeService();
            try {
                String responseMsg = scopeService.deleteScope(scopeName);
                response = ResponseBuilder.createOkResponse(responseMsg);
            } catch (OAuthException e) {
                invokeExceptionHandler(e, req);
                response = ResponseBuilder.createResponse(e.getHttpStatus(), e.getMessage());
            }
        } else {
            response = ResponseBuilder.createNotFoundResponse();
        }
        return response;
    }

    ScopeService getScopeService() {
        return new ScopeService();
    }

    @DocOAuth20Sub(name = "handleUpdateClientApplication", dec = "更新单个application", method = "PUT", url = "/oauth2.0/applications/{LOCAL_NODE_ID}", args = {
            @DocOAuth20SubIn(name = "description", dec = "用户自定义描述", require = true, type = String.class),
            @DocOAuth20SubIn(name = "scope", dec = "支持由空格分割的多个scope", require = true, type = String.class),
            @DocOAuth20SubIn(name = "status", dec = "值为1或者0,1为有效，0为无效", require = true, type = Integer.class),
            @DocOAuth20SubIn(name = "client_id", dec = "client_id", require = true, type = String.class),
            @DocOAuth20SubIn(name = "application_details", dec = "用户自定义的多个键值对", require = false, type = Map.class)
    })
    FullHttpResponse handleUpdateClientApplication(FullHttpRequest req) {
        FullHttpResponse response = null;
        Matcher m = APPLICATION_PATTERN.matcher(req.uri());
        if (m.find()) {
            String clientId = m.group(1);
            try {
                if (auth.updateClientApp(req, clientId)) {
                    response = ResponseBuilder.createOkResponse(ResponseBuilder.CLIENT_APP_UPDATED);
                }
            } catch (OAuthException ex) {
                response = ResponseBuilder.createOAuthExceptionResponse(ex);
                invokeExceptionHandler(ex, req);
            }
        } else {
            response = ResponseBuilder.createNotFoundResponse();
        }
        return response;
    }

    @DocOAuth20Sub(name = "handleGetAllClientApplications", dec = "获取所有application", method = "GET", url = "/oauth2.0/applications", args = {
            @DocOAuth20SubIn(name = "status", dec = "非必填，若填写则获取对应status的application", require = false, type = Integer.class)})
    FullHttpResponse handleGetAllClientApplications(FullHttpRequest req) {
        FullHttpResponse response;
        try {
            List<ApplicationInfo> apps = filterClientApps(req, DBManagerFactory.getInstance().getAllApplications());
            String jsonString = JSON.toJSONString(apps);
            response = ResponseBuilder.createOkResponse(jsonString);
        } catch (Exception e) {
            LOG.error("cannot list client applications", e);
            invokeExceptionHandler(e, req);
            response = ResponseBuilder.createResponse(HttpResponseStatus.BAD_REQUEST,
                    ResponseBuilder.CANNOT_LIST_CLIENT_APPS);
        }
        return response;
    }

    List<ApplicationInfo> filterClientApps(FullHttpRequest req, List<ApplicationInfo> apps) {
        List<ApplicationInfo> filteredApps = new ArrayList<>();
        QueryStringDecoder dec = new QueryStringDecoder(req.uri());
        Map<String, List<String>> params = dec.parameters();
        if (params != null) {
            String status = QueryParameter.getFirstElement(params, "status");
            Integer statusInt;
            if (status != null && !status.isEmpty()) {
                try {
                    statusInt = Integer.valueOf(status);
                    for (ApplicationInfo app : apps) {
                        if (Objects.equals(app.getStatus(), statusInt)) {
                            filteredApps.add(app);
                        }
                    }
                } catch (NumberFormatException e) {
                    // status is invalid, ignore it
                    filteredApps = Collections.unmodifiableList(apps);
                }
            } else {
                filteredApps = Collections.unmodifiableList(apps);
            }
        }
        return filteredApps;
    }

    @DocOAuth20Sub(name = "handleGetAccessTokens", dec = "获取所有access_token的信息", method = "GET", url = "/oauth2.0/tokens", args = {
            @DocOAuth20SubIn(name = "client_id", dec = "client_id", require = true, type = String.class),
            @DocOAuth20SubIn(name = "user_id", dec = "用户获取code时自定义的user_id", require = true, type = String.class)
    })
    FullHttpResponse handleGetAccessTokens(FullHttpRequest req) {
        FullHttpResponse response;
        QueryStringDecoder dec = new QueryStringDecoder(req.uri());
        Map<String, List<String>> params = dec.parameters();
        String clientId = QueryParameter.getFirstElement(params, QueryParameter.CLIENT_ID);
        String userId = QueryParameter.getFirstElement(params, QueryParameter.USER_ID);
        if (clientId == null || clientId.isEmpty()) {
            response = ResponseBuilder.createBadRequestResponse(
                    String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, QueryParameter.CLIENT_ID));
        } else if (userId == null || userId.isEmpty()) {
            response = ResponseBuilder.createBadRequestResponse(
                    String.format(ResponseBuilder.MANDATORY_PARAM_MISSING, QueryParameter.USER_ID));
        } else {
            // check that LOCAL_NODE_ID exists, no matter whether it is active or not
            if (!auth.isExistingClient(clientId)) {
                response = ResponseBuilder.createBadRequestResponse(ResponseBuilder.INVALID_CLIENT_ID);
            } else {
                AccessToken accessTokens = DBManagerFactory.getInstance().getAccessTokenByUserIdAndClientId(userId,
                        clientId);
                String jsonString = JSON.toJSONString(accessTokens);
                response = ResponseBuilder.createOkResponse(jsonString);
            }
        }
        return response;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest) msg;
        FullHttpResponse response;
        try {
            response = handle(req);
        } catch (Throwable e) {
            LOG.error("oauth未知异常", e);
            String jsonPayload = new JSONObject() {
                {
                    put("error", "unknown exception");
                }
            }.toJSONString();
            response = ResponseBuilder.createResponse(HttpResponseStatus.EXPECTATION_FAILED, jsonPayload);
        }
        ctx.writeAndFlush(response).addListener(future -> {
            LOG.info("关闭连接!");
            ctx.close();
        });
    }
}
