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

import com.alibaba.fastjson.annotation.JSONField;
import com.apifest.oauth20.utils.JsonUtils;
import com.apifest.oauth20.utils.RandomGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Holds a client application information as it is stored in the DB.
 *
 * @author Rossitsa Borissova
 */
public class ClientCredentials implements Serializable {

    private static final long serialVersionUID = 6443754960051591393L;

    public static final int ACTIVE_STATUS = 1;
    public static final int INACTIVE_STATUS = 0;

    @JSONField(name = "client_id", ordinal = 1)
    private String id;

    @JSONField(name = "client_secret", ordinal = 2)
    private String secret;

    // scopes are separated by space
    @JSONField
    private String scope;

    @JSONField
    private String name;

    @JSONField
    private Long created;

    @JSONField(name = "redirect_uri")
    private String uri;

    @JSONField(name = "description")
    private String descr;

    // client types - public or confidential
    @JSONField
    private int type;

    // 1 - active, 0 - not active
    @JSONField
    private int status;

    @JSONField(serialize = false, deserialize = false)
    private Map<String, String> applicationDetails = null;

    public ClientCredentials(String appName, String scope, String description, String uri, Map<String, String> applicationDetails) {
        this.name = appName;
        this.scope = scope;
        this.descr = (description != null) ? description : "";
        this.uri = uri;
        this.id = generateClientId();
        this.secret = generateClientSecret();
        this.created = (new Date()).getTime();
        this.status = INACTIVE_STATUS;
        this.applicationDetails = applicationDetails;
    }

    public ClientCredentials(String appName, String scope, String description, String uri, String clientId, String clientSecret,
                             Map<String, String> applicationDetails) {
        this.name = appName;
        this.scope = scope;
        this.descr = (description != null) ? description : "";
        this.uri = uri;
        this.id = clientId;
        this.secret = clientSecret;
        this.created = (new Date()).getTime();
        this.status = INACTIVE_STATUS;
        this.applicationDetails = applicationDetails;
    }

    public ClientCredentials() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescr() {
        return descr;
    }

    public ClientCredentials setDescr(String descr) {
        this.descr = descr;
        return this;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public ClientCredentials setStatus(int status) {
        this.status = status;
        return this;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getScope() {
        return scope;
    }

    public ClientCredentials setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public Map<String, String> getApplicationDetails() {
        return applicationDetails;
    }

    public ClientCredentials setApplicationDetails(Map<String, String> applicationDetails) {
        this.applicationDetails = applicationDetails;
        return this;
    }

    private String generateClientId() {
        return RandomGenerator.generateShortRandomString();
    }

    private String generateClientSecret() {
        return RandomGenerator.generateRandomString();
    }

    /**
     * Used to create an instance when a record from DB is loaded.
     *
     * @param map Map that contains the record info
     * @return instance of ClientCredentials
     */
    public static ClientCredentials loadFromMap(Map<String, Object> map) {
        ClientCredentials creds = new ClientCredentials();
        creds.name = (String) map.get("name");
        creds.id = (String) map.get("_id");
        creds.secret = (String) map.get("secret");
        creds.uri = (String) map.get("uri");
        creds.descr = (String) map.get("descr");
        creds.type = ((Integer) map.get("type")).intValue();
        creds.status = ((Integer) map.get("status")).intValue();
        creds.created = (Long) map.get("created");
        creds.scope = (String) map.get("scope");
        if (map.get("applicationDetails") != null) {
            creds.applicationDetails = JsonUtils.convertStringToMap(map.get("applicationDetails").toString());
        }
        return creds;
    }

    public static ClientCredentials loadFromStringMap(Map<String, String> map) {
        ClientCredentials creds = new ClientCredentials();
        creds.name = map.get("name");
        creds.id = map.get("_id");
        creds.secret = map.get("secret");
        creds.uri = map.get("uri");
        creds.descr = map.get("descr");
        creds.type = Integer.valueOf(map.get("type"));
        creds.status = Integer.valueOf(map.get("status"));
        creds.created = Long.valueOf(map.get("created"));
        creds.scope = map.get("scope");
        // TODO: check whether details is the name of the field
        creds.applicationDetails = JsonUtils.convertStringToMap(map.get("details"));
        return creds;
    }

    public static ClientCredentials loadFromStringList(List<String> list) {
        ClientCredentials creds = new ClientCredentials();
        creds.id = list.get(0);
        creds.secret = list.get(1);
        creds.name = list.get(2);
        ;
        creds.uri = list.get(3);
        creds.descr = list.get(4);
        creds.type = Integer.valueOf(list.get(5));
        creds.status = Integer.valueOf(list.get(6));
        creds.created = Long.valueOf(list.get(7));
        creds.scope = list.get(8);
        // TODO: check whether details is the name of the field
        creds.applicationDetails = JsonUtils.convertStringToMap(list.get(9));
        return creds;
    }

}
