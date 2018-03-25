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

package com.apifest.oauth20.persistence.redis;

import com.alibaba.fastjson.JSON;
import com.apifest.oauth20.bean.ApplicationInfo;
import com.apifest.oauth20.bean.AuthCode;
import com.apifest.oauth20.bean.ClientCredentials;
import com.apifest.oauth20.bean.Scope;
import com.apifest.oauth20.persistence.DBManager;
import info.xiancloud.core.support.authen.AccessToken;
import info.xiancloud.core.support.cache.api.CacheMapUtil;
import info.xiancloud.core.support.cache.api.CacheObjectUtil;
import info.xiancloud.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Apostol Terziev, happyyangyuan
 */
public class RedisDBManager implements DBManager {

    public static final String CLIENT_CREDENTIALS_KEY = "oauth2.0-clientCredentials";
    public static final String SCOPES_KEY = "oauth2.0-scopes";

    @Override
    public boolean validClient(String clientId, String clientSecret) {
        ClientCredentials clientCredentials = findClientCredentials(clientId);
        if (clientCredentials != null && clientSecret.equals(clientCredentials.getSecret())
                && ClientCredentials.ACTIVE_STATUS == clientCredentials.getStatus()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void storeClientCredentials(ClientCredentials clientCreds) {
        CacheMapUtil.put(CLIENT_CREDENTIALS_KEY, clientCreds.getId(), clientCreds);
    }

    @Override
    public ClientCredentials findClientCredentials(String clientId) {
        return CacheMapUtil.get(CLIENT_CREDENTIALS_KEY, clientId, ClientCredentials.class);
    }

    @Override
    public boolean updateClientCredentials(String clientId, String scope, String description, Integer status,
                                           Map<String, String> applicationDetails) {
        ClientCredentials clientCredentials = CacheMapUtil.get(CLIENT_CREDENTIALS_KEY, clientId,
                ClientCredentials.class);
        if (!StringUtil.isEmpty(scope)) {
            clientCredentials.setScope(scope);
        }
        if (!StringUtil.isEmpty(description)) {
            clientCredentials.setDescr(description);
        }
        if (status != null) {
            clientCredentials.setStatus(status);
        }
        if (applicationDetails != null && !applicationDetails.isEmpty()) {
            clientCredentials.setApplicationDetails(applicationDetails);
        }
        return CacheMapUtil.put(CLIENT_CREDENTIALS_KEY, clientId, clientCredentials) == null ? false : true;
    }

    @Override
    public List<ApplicationInfo> getAllApplications() {
        List<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
        Collection<String> values = CacheMapUtil.values(CLIENT_CREDENTIALS_KEY);
        for (String json : values) {
            ClientCredentials creds = JSON.parseObject(json, ClientCredentials.class);
            ApplicationInfo app = ApplicationInfo.loadFromClientCredentials(creds);
            if (app != null) {
                list.add(app);
            }
        }
        return list;
    }

    @Override
    public void storeAuthCode(AuthCode authCode) {
        CacheObjectUtil.set("acc:" + authCode.getCode(), authCode, 1800);
        // RemoteCacheObject.set("acuri:" + authCode.getCode() +
        // authCode.getRedirectUri(), authCode.getCode(), 1800);
    }

    @Override
    public AuthCode findAuthCode(String authCode/* , String redirectUri */) {
        return CacheObjectUtil.get("acc:" + authCode, AuthCode.class);
    }

    @Override
    public void updateAuthCodeValidStatus(String authCode, boolean valid) {
        AuthCode updatedAuthCode = CacheObjectUtil.get("acc:" + authCode, AuthCode.class).setValid(valid);
        CacheObjectUtil.set("acc:" + authCode, updatedAuthCode);
    }

    @Override
    public void storeAccessToken(AccessToken accessToken) {
        Integer tokenExpirationInSeconds = Integer.valueOf((!accessToken.getRefreshExpiresIn().isEmpty())
                ? accessToken.getRefreshExpiresIn() : accessToken.getExpiresIn());
        CacheObjectUtil.set("at:" + accessToken.getToken(), accessToken, tokenExpirationInSeconds);
        CacheObjectUtil.set("atr:" + accessToken.getRefreshToken() + accessToken.getClientId(),
                accessToken.getToken());
        // TODO 目前只支持同一个user在一个第三方app内只存储一个token
        CacheObjectUtil.set("atuid:" + accessToken.getUserId() + ":" + accessToken.getClientId(),
                accessToken.getToken(), tokenExpirationInSeconds);
    }

    @Override
    public AccessToken findAccessTokenByRefreshToken(String refreshToken, String clientId) {
        String accessTokenStr = CacheObjectUtil.get("atr:" + refreshToken + clientId, String.class);
        if (!StringUtil.isEmpty(accessTokenStr)) {
            return CacheObjectUtil.get("at:" + accessTokenStr, AccessToken.class);
        } else {
            return null;
        }
    }

    @Override
    public void updateAccessTokenValidStatus(String accessToken, boolean valid) {
        AccessToken updatedTokenObject = CacheObjectUtil.get("at:" + accessToken, AccessToken.class).setValid(valid);
        CacheObjectUtil.set("at:" + accessToken, updatedTokenObject);
    }

    @Override
    public AccessToken findAccessToken(String accessToken) {
        return CacheObjectUtil.get("at:" + accessToken, AccessToken.class);
    }

    @Override
    public void removeAccessToken(String accessToken) {
        CacheObjectUtil.remove("at:" + accessToken);
    }

    @Override
    public AccessToken getAccessTokenByUserIdAndClientId(String userId, String clientId) {
        String tokenStr = CacheObjectUtil.get("atuid:" + userId + ":" + clientId, String.class);
        return CacheObjectUtil.get("at:" + tokenStr, AccessToken.class);
    }

    @Override
    public boolean storeScope(Scope scope) {
        CacheMapUtil.put(SCOPES_KEY, scope.getScope(), scope);
        // Redis.hset() method calling allways success
        return true;
    }

    @Override
    public List<Scope> getAllScopes() {
        return CacheMapUtil.values(SCOPES_KEY).stream().map(value -> JSON.parseObject(value, Scope.class))
                .collect(Collectors.toList());
    }

    @Override
    public Scope findScope(String scopeName) {
        return CacheMapUtil.get(SCOPES_KEY, scopeName, Scope.class);
    }

    @Override
    public boolean deleteScope(String scopeName) {
        return CacheMapUtil.remove(SCOPES_KEY, scopeName);
    }
}
