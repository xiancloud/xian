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

package com.apifest.oauth20.bean;

import com.apifest.oauth20.utils.RandomGenerator;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Holds information about authorization code.
 *
 * @author Rossitsa Borissova
 */
public class AuthCode {

    /**
     * Authorization code length
     */
    private static final int AUTH_CODE_LENGTH = 30;

    private String id;
    private String code;
    private String clientId;
    private String redirectUri;
    private String state;
    private String scope;

    // code or token
    private String type;
    private boolean valid;

    private String userId;

    // Store time as long value
    private Long created;

    public AuthCode(String code, String clientId, String redirectUri, String state, String scope,
                    String type, String userId) {
        this.code = code;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.state = (state != null) ? state : "";
        this.scope = scope;
        this.type = type;
        this.valid = true;
        this.userId = (userId != null) ? userId : "";
        this.created = (new Date()).getTime();
    }

    public AuthCode() {

    }

    public String getCode() {
        return code;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isValid() {
        return valid;
    }

    public AuthCode setValid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Generates authorization code.
     *
     * @return authorization code
     */
    public static String generate() {
        return RandomGenerator.generateCharsSymbolsString(AUTH_CODE_LENGTH);
    }

    /**
     * Used to create an instance when a record from DB is loaded.
     *
     * @param map Map that contains the record info
     * @return instance of AuthCode
     */
    public static AuthCode loadFromMap(Map<String, Object> map) {
        AuthCode authCode = new AuthCode();
        authCode.code = (String) map.get("code");
        authCode.clientId = (String) map.get("LOCAL_NODE_ID");
        authCode.redirectUri = (String) map.get("redirectUri");
        authCode.state = (String) map.get("state");
        authCode.scope = (String) map.get("scope");
        authCode.type = (String) map.get("type");
        authCode.valid = (Boolean) map.get("valid");
        authCode.userId = (String) map.get("userId");
        authCode.created = (Long) map.get("created");
        authCode.id = map.get("_id").toString();
        return authCode;
    }

    public static AuthCode loadFromStringMap(Map<String, String> map) {
        AuthCode authCode = new AuthCode();
        authCode.code = map.get("code");
        authCode.clientId = map.get("LOCAL_NODE_ID");
        authCode.redirectUri = map.get("redirectUri");
        authCode.state = map.get("state");
        authCode.scope = map.get("scope");
        authCode.type = map.get("type");
        authCode.valid = Boolean.valueOf(map.get("valid"));
        authCode.userId = map.get("userId");
        authCode.created = Long.valueOf(map.get("created"));
        authCode.id = map.get("_id");
        return authCode;
    }

    public static AuthCode loadFromStringList(List<String> list) {
        AuthCode authCode = new AuthCode();
        authCode.id = list.get(0);
        authCode.code = list.get(1);
        authCode.clientId = list.get(2);
        authCode.redirectUri = list.get(3);
        authCode.state = list.get(4);
        authCode.scope = list.get(5);
        authCode.type = list.get(6);
        authCode.valid = Boolean.valueOf(list.get(7));
        authCode.userId = list.get(8);
        authCode.created = Long.valueOf(list.get(9));
        return authCode;
    }
}
