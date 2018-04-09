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
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.ArrayList;
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
    public Single<Boolean> validClient(String clientId, String clientSecret) {
        return findClientCredentials(clientId)
                .map(clientCredentials -> clientCredentials != null && clientSecret.equals(clientCredentials.getSecret())
                        && ClientCredentials.ACTIVE_STATUS == clientCredentials.getStatus())
                .switchIfEmpty(Single.just(false))
                ;

    }

    @Override
    public Completable storeClientCredentials(ClientCredentials clientCreds) {
        return CacheMapUtil.put(CLIENT_CREDENTIALS_KEY, clientCreds.getId(), clientCreds).toCompletable();
    }

    @Override
    public Maybe<ClientCredentials> findClientCredentials(String clientId) {
        return CacheMapUtil.get(CLIENT_CREDENTIALS_KEY, clientId, ClientCredentials.class);
    }

    @Override
    public Single<Boolean> updateClientCredentials(String clientId, String scope, String description, Integer status, Map<String, String> applicationDetails) {
        return CacheMapUtil
                .get(CLIENT_CREDENTIALS_KEY, clientId, ClientCredentials.class)
                .flatMapSingle(clientCredentials -> {
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
                    return CacheMapUtil
                            .put(CLIENT_CREDENTIALS_KEY, clientId, clientCredentials);
                });
    }

    @Override
    public Single<List<ApplicationInfo>> getAllApplications() {
        List<ApplicationInfo> list = new ArrayList<>();
        return CacheMapUtil
                .values(CLIENT_CREDENTIALS_KEY)
                .switchIfEmpty(Single.just(new ArrayList<>()))
                .map(values -> {
                    for (String json : values) {
                        ClientCredentials credentials = JSON.parseObject(json, ClientCredentials.class);
                        ApplicationInfo app = ApplicationInfo.loadFromClientCredentials(credentials);
                        if (app != null) {
                            list.add(app);
                        }
                    }
                    return list;
                });
    }

    @Override
    public Completable storeAuthCode(AuthCode authCode) {
        return CacheObjectUtil.set("acc:" + authCode.getCode(), authCode, 1800);
        // RemoteCacheObject.set("acuri:" + authCode.getCode() +
        // authCode.getRedirectUri(), authCode.getCode(), 1800);
    }

    @Override
    public Maybe<AuthCode> findAuthCode(String authCode/* , String redirectUri */) {
        return CacheObjectUtil.get("acc:" + authCode, AuthCode.class);
    }

    @Override
    public Completable updateAuthCodeValidStatus(String authCode, boolean valid) {
        return CacheObjectUtil
                .get("acc:" + authCode, AuthCode.class)
                .map(updatedAuthCode -> {
                    updatedAuthCode.setValid(valid);
                    return updatedAuthCode;
                })
                .flatMapCompletable(updatedAuthCode -> CacheObjectUtil.set("acc:" + authCode, updatedAuthCode));
    }

    @Override
    public Completable storeAccessToken(AccessToken accessToken) {
        Integer tokenExpirationInSeconds = Integer.valueOf((!accessToken.getRefreshExpiresIn().isEmpty())
                ? accessToken.getRefreshExpiresIn() : accessToken.getExpiresIn());
        return CacheObjectUtil
                .set("at:" + accessToken.getToken(), accessToken, tokenExpirationInSeconds)
                .andThen(CacheObjectUtil.set("atr:" + accessToken.getRefreshToken() + accessToken.getClientId(), accessToken.getToken()))
                .andThen(
                        // TODO 目前只支持同一个user在一个第三方application内只存储一个token
                        CacheObjectUtil.set("atuid:" + accessToken.getUserId() + ":" + accessToken.getClientId(),
                                accessToken.getToken(), tokenExpirationInSeconds));
    }

    @Override
    public Maybe<AccessToken> findAccessTokenByRefreshToken(String refreshToken, String clientId) {
        return CacheObjectUtil.get("atr:" + refreshToken + clientId, String.class)
                .flatMap(accessTokenStr -> {
                    if (!StringUtil.isEmpty(accessTokenStr)) {
                        return CacheObjectUtil
                                .get("at:" + accessTokenStr, AccessToken.class);
                    } else {
                        return Maybe.empty();
                    }
                });
    }

    @Override
    public Completable updateAccessTokenValidStatus(String accessToken, boolean valid) {
        return CacheObjectUtil.get("at:" + accessToken, AccessToken.class)
                .map(updatedTokenObject -> updatedTokenObject.setValid(valid))
                .flatMapCompletable(updatedTokenObject -> CacheObjectUtil.set("at:" + accessToken, updatedTokenObject));
    }

    @Override
    public Maybe<AccessToken> findAccessToken(String accessToken) {
        return CacheObjectUtil.get("at:" + accessToken, AccessToken.class);
    }

    @Override
    public Completable removeAccessToken(String accessToken) {
        return CacheObjectUtil.remove("at:" + accessToken).toCompletable();
    }

    @Override
    public Maybe<AccessToken> getAccessTokenByUserIdAndClientId(String userId, String clientId) {
        return CacheObjectUtil.get("atuid:" + userId + ":" + clientId, String.class)
                .flatMap(tokenStr -> CacheObjectUtil.get("at:" + tokenStr, AccessToken.class));
    }

    @Override
    public Single<Boolean> storeScope(Scope scope) {
        return CacheMapUtil.put(SCOPES_KEY, scope.getScope(), scope);
        // Redis.hset() method calling allways success
    }

    @Override
    public Single<List<Scope>> getAllScopes() {
        return CacheMapUtil.values(SCOPES_KEY)
                .map(values -> values.stream().map(value -> JSON.parseObject(value, Scope.class))
                        .collect(Collectors.toList()))
                .switchIfEmpty(Single.just(new ArrayList<>()));
    }

    @Override
    public Maybe<Scope> findScope(String scopeName) {
        return CacheMapUtil.get(SCOPES_KEY, scopeName, Scope.class);
    }

    @Override
    public Single<Boolean> deleteScope(String scopeName) {
        return CacheMapUtil.remove(SCOPES_KEY, scopeName);
    }
}
