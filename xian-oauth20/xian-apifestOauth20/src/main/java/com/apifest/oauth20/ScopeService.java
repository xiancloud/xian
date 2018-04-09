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
import com.apifest.oauth20.bean.ApplicationInfo;
import com.apifest.oauth20.bean.ClientCredentials;
import com.apifest.oauth20.bean.OAuthException;
import com.apifest.oauth20.bean.Scope;
import com.apifest.oauth20.bean.token_request.TokenRequest;
import com.apifest.oauth20.conf.OAuthConfig;
import com.apifest.oauth20.persistence.DBManager;
import com.apifest.oauth20.persistence.DBManagerFactory;
import com.apifest.oauth20.utils.ResponseBuilder;
import com.apifest.oauth20.validator.InputValidator;
import info.xiancloud.core.util.LOG;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.reactivex.Maybe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Responsible for storing and loading OAuth20 scopes.
 *
 * @author Rossitsa Borissova
 */
public class ScopeService {

    protected static final String MANDATORY_FIELDS_ERROR = "{\"error\":\"scope, description, cc_expires_in and pass_expires_in are mandatory\"}";
    protected static final String MANDATORY_SCOPE_ERROR = "{\"error\":\"scope is mandatory\"}";
    protected static final String SCOPE_NAME_INVALID_ERROR = "{\"error\":\"scope name not valid - it may contain aplha-numeric, - and _\"}";
    protected static final String SCOPE_STORED_OK_MESSAGE = "{\"status\":\"scope successfully stored\"}";
    protected static final String SCOPE_STORED_NOK_MESSAGE = "{\"status\":\"scope not stored\"}";
    protected static final String SCOPE_UPDATED_OK_MESSAGE = "{\"status\":\"scope successfully updated\"}";
    protected static final String SCOPE_UPDATED_NOK_MESSAGE = "{\"status\":\"scope not updated\"}";
    protected static final String SCOPE_NOT_EXIST = "{\"status\":\"scope does not exist\"}";
    protected static final String SCOPE_ALREADY_EXISTS = "{\"status\":\"scope already exists\"}";
    protected static final String SCOPE_DELETED_OK_MESSAGE = "{\"status\":\"scope successfully deleted\"}";
    protected static final String SCOPE_DELETED_NOK_MESSAGE = "{\"status\":\"scope not deleted\"}";
    protected static final String SCOPE_USED_BY_APP_MESSAGE = "{\"status\":\"scope cannot be deleted, there are client apps registered with it\"}";
    private static final String SPACE = " ";

    /**
     * Register an oauth scope. If the scope already exists, returns an error.
     *
     * @param req http request
     * @return String message that will be returned in the response
     */
    public String registerScope(FullHttpRequest req) throws OAuthException {
        String contentType = (req.headers() != null) ? req.headers().get(HttpHeaderNames.CONTENT_TYPE) : null;
        // check Content-Type
        if (contentType != null && contentType.contains(ResponseBuilder.APPLICATION_JSON)) {
            try {
                Scope scope = InputValidator.validate(req.content().toString(CharsetUtil.UTF_8), Scope.class);
                if (scope.valid()) {
                    if (!Scope.validScopeName(scope.getScope())) {
                        LOG.error("scope name is not valid");
                        throw new OAuthException(SCOPE_NAME_INVALID_ERROR, HttpResponseStatus.BAD_REQUEST);
                    }
                    LOG.info(">>>>>>>>>>>>>>> scope = " + scope);
                    Scope foundScope = DBManagerFactory.getInstance().findScope(scope.getScope()).blockingGet();
                    if (foundScope != null) {
                        LOG.error("scope already exists");
                        throw new OAuthException(SCOPE_ALREADY_EXISTS, HttpResponseStatus.BAD_REQUEST);
                    } else {
                        // store in the DB, if already exists such a scope, overwrites it
                        DBManagerFactory.getInstance().storeScope(scope).blockingGet();
                    }
                } else {
                    LOG.error("scope is not valid");
                    throw new OAuthException(MANDATORY_FIELDS_ERROR, HttpResponseStatus.BAD_REQUEST);
                }
            } catch (Throwable e) {
                LOG.error("cannot handle scope request", e);
                throw new OAuthException(e, null, HttpResponseStatus.BAD_REQUEST);
            }
        } else {
            throw new OAuthException(ResponseBuilder.UNSUPPORTED_MEDIA_TYPE, HttpResponseStatus.BAD_REQUEST);
        }
        return SCOPE_STORED_OK_MESSAGE;
    }

    /**
     * Returns either all scopes or scopes for a specific client_id passed as query parameter.
     *
     * @param req request
     * @return string If query param client_id is passed, then the scopes for that client_id will be returned.
     * Otherwise, all available scopes will be returned in JSON format.
     */
    public String getScopes(HttpRequest req) throws OAuthException {
        QueryStringDecoder dec = new QueryStringDecoder(req.uri());
        Map<String, List<String>> queryParams = dec.parameters();
        if (queryParams.containsKey("client_id")) {
            return getScopes(queryParams.get("client_id").get(0));
        }
        List<Scope> scopes = DBManagerFactory.getInstance().getAllScopes().blockingGet();
        String jsonString;
        try {
            jsonString = JSON.toJSONString(scopes);
        } catch (Exception e) {
            LOG.error("cannot load scopes", e);
            throw new OAuthException(e, null, HttpResponseStatus.BAD_REQUEST);
        }
        return jsonString;
    }

    /**
     * Checks whether a scope is valid for a given client id.
     *
     * @param scope    oauth scope
     * @param clientId client id
     * @return the scope if it is valid, otherwise returns null
     */
    public Maybe<String> getValidScope(String scope, String clientId) {
        return DBManagerFactory.getInstance()
                .findClientCredentials(clientId)
                .map(creds -> getValidScopeByScope(scope, creds.getScope()));
    }

    public String getValidScopeByScope(String scope, String storedScope) {
        String validScope = null;
        if (scope == null || scope.length() == 0) {
            // get client scope
            validScope = storedScope;
        } else {
            // check that scope exists and is allowed for that client app
            boolean scopeOk = scopeAllowed(scope, storedScope);
            if (scopeOk) {
                validScope = scope;
            }
        }
        return validScope;
    }

    /**
     * Checks whether a scope is contained in allowed scopes.
     *
     * @param scope         scope to be checked
     * @param allowedScopes all allowed scopes
     * @return true if the scope is allowed, otherwise false
     */
    public boolean scopeAllowed(String scope, String allowedScopes) {
        String[] allScopes = allowedScopes.split(SPACE);
        List<String> allowedList = Arrays.asList(allScopes);
        String[] scopes = scope.split(SPACE);
        int allowedCount = 0;
        for (String s : scopes) {
            if (allowedList.contains(s)) {
                allowedCount++;
            }
        }
        return (allowedCount == scopes.length);
    }

    /**
     * Returns value for expires_in by given scope and token type.
     *
     * @param scope          scope/s for which expires in will be returned
     * @param tokenGrantType client_credentials or password type
     * @return minimum value of given scope/s expires_in
     */
    public int getExpiresIn(String tokenGrantType, String scope) {
        int expiresIn = Integer.MAX_VALUE;
        List<Scope> scopes = loadScopes(scope);
        boolean ccGrantType = TokenRequest.CLIENT_CREDENTIALS.equals(tokenGrantType);
        if (TokenRequest.CLIENT_CREDENTIALS.equals(tokenGrantType)) {
            for (Scope s : scopes) {
                if (s.getCcExpiresIn() < expiresIn) {
                    expiresIn = s.getCcExpiresIn();
                }
            }
        } else if (TokenRequest.PASSWORD.equals(tokenGrantType)) {
            for (Scope s : scopes) {
                if (s.getPassExpiresIn() < expiresIn) {
                    expiresIn = s.getPassExpiresIn();
                }
            }
        } else {
            // refresh_token
            for (Scope s : scopes) {
                if (s.getRefreshExpiresIn() < expiresIn) {
                    expiresIn = s.getRefreshExpiresIn();
                }
            }
        }
        if (scopes.size() == 0 || expiresIn == Integer.MAX_VALUE) {
            expiresIn = (ccGrantType) ? OAuthConfig.DEFAULT_CC_EXPIRES_IN : OAuthConfig.DEFAULT_PASSWORD_EXPIRES_IN;
        }
        return expiresIn;
    }

    /**
     * Updates a scope. If the scope does not exists, returns an error.
     *
     * @param req http request
     * @return String message that will be returned in the response
     */
    public String updateScope(FullHttpRequest req, String scopeName) throws OAuthException {
        String contentType = (req.headers() != null) ? req.headers().get(HttpHeaderNames.CONTENT_TYPE) : null;
        // check Content-Type
        if (contentType != null && contentType.contains(ResponseBuilder.APPLICATION_JSON)) {
            try {
                Scope scope = InputValidator.validate(req.content().toString(CharsetUtil.UTF_8), Scope.class);
                if (scope.validForUpdate()) {
                    Scope foundScope = DBManagerFactory.getInstance().findScope(scopeName).blockingGet();
                    if (foundScope == null) {
                        LOG.error("scope does not exist");
                        throw new OAuthException(SCOPE_NOT_EXIST, HttpResponseStatus.BAD_REQUEST);
                    } else {
                        setScopeEmptyValues(scope, foundScope);
                        DBManagerFactory.getInstance().storeScope(scope);
                    }
                } else {
                    LOG.error("scope is not valid");
                    throw new OAuthException(MANDATORY_SCOPE_ERROR, HttpResponseStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                LOG.error("cannot handle scope request", e);
                throw new OAuthException(e, null, HttpResponseStatus.BAD_REQUEST);
            }
        } else {
            throw new OAuthException(ResponseBuilder.UNSUPPORTED_MEDIA_TYPE, HttpResponseStatus.BAD_REQUEST);
        }
        return SCOPE_UPDATED_OK_MESSAGE;
    }

    /**
     * Deletes a scope. If the scope does not exists, returns an error.
     *
     * @param scopeName scopeName
     * @return String message that will be returned in the response
     */
    public String deleteScope(String scopeName) throws OAuthException {
        String responseMsg;
        Scope foundScope = DBManagerFactory.getInstance().findScope(scopeName).blockingGet();
        if (foundScope == null) {
            LOG.error("scope does not exist");
            throw new OAuthException(SCOPE_NOT_EXIST, HttpResponseStatus.BAD_REQUEST);
        } else {
            // first, check whether there is a client app registered with that scope
            List<ApplicationInfo> registeredApps = getClientAppsByScope(scopeName);
            if (registeredApps.size() > 0) {
                responseMsg = SCOPE_USED_BY_APP_MESSAGE;
            } else {
                boolean ok = DBManagerFactory.getInstance().deleteScope(scopeName).blockingGet();
                if (ok) {
                    responseMsg = SCOPE_DELETED_OK_MESSAGE;
                } else {
                    responseMsg = SCOPE_DELETED_NOK_MESSAGE;
                }
            }
        }
        return responseMsg;
    }

    public String getScopeByName(String scopeName) throws OAuthException {
        String jsonString;
        Scope scope = DBManagerFactory.getInstance().findScope(scopeName).blockingGet();
        if (scope != null) {
            try {
                jsonString = JSON.toJSONString(scope);
            } catch (Exception e) {
                LOG.error("cannot load scopes", e);
                throw new OAuthException(e, null, HttpResponseStatus.BAD_REQUEST);
            }
        } else {
            throw new OAuthException(SCOPE_NOT_EXIST, HttpResponseStatus.NOT_FOUND);
        }
        return jsonString;
    }

    protected List<ApplicationInfo> getClientAppsByScope(String scopeName) {
        List<ApplicationInfo> scopeApps = new ArrayList<>();
        List<ApplicationInfo> allApps = DBManagerFactory.getInstance().getAllApplications().blockingGet();
        for (ApplicationInfo app : allApps) {
            if (app.getScope() != null && app.getScope().contains(scopeName)) {
                scopeApps.add(app);
                break;
            }
        }
        return scopeApps;
    }

    protected void setScopeEmptyValues(Scope scope, Scope foundScope) {
        // if some fields are null, keep the old values
        scope.setScope(foundScope.getScope());
        if (scope.getDescription() == null || scope.getDescription().length() == 0) {
            scope.setDescription(foundScope.getDescription());
        }
        if (scope.getCcExpiresIn() == null) {
            scope.setCcExpiresIn(foundScope.getCcExpiresIn());
        }
        if (scope.getPassExpiresIn() == null) {
            scope.setPassExpiresIn(foundScope.getPassExpiresIn());
        }
        if (scope.getRefreshExpiresIn() == null) {
            scope.setRefreshExpiresIn(foundScope.getRefreshExpiresIn());
        }
    }

    protected List<Scope> loadScopes(String scope) {
        String[] scopes = scope.split(SPACE);
        List<Scope> loadedScopes = new ArrayList<>();
        DBManager db = DBManagerFactory.getInstance();
        for (String name : scopes) {
            loadedScopes.add(db.findScope(name).blockingGet());
        }
        return loadedScopes;
    }

    protected String getScopes(String clientId) throws OAuthException {
        ClientCredentials credentials = DBManagerFactory.getInstance().findClientCredentials(clientId).blockingGet();
        String jsonString;
        if (credentials != null) {
            //scopes are separated by comma
            String scopes = credentials.getScope();
            String[] s = scopes.split(SPACE);
            List<Scope> result = new ArrayList<>();
            for (String name : s) {
                Scope scope = DBManagerFactory.getInstance().findScope(name).blockingGet();
                result.add(scope);
            }

            try {
                jsonString = JSON.toJSONString(result);
            } catch (Exception e) {
                LOG.error("cannot load scopes per LOCAL_NODE_ID", e);
                throw new OAuthException(e, null, HttpResponseStatus.BAD_REQUEST);
            }
        } else {
            throw new OAuthException(null, HttpResponseStatus.NOT_FOUND);
        }
        return jsonString;
    }
}
