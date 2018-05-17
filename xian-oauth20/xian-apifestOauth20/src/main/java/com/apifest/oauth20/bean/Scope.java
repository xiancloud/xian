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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an OAuth20 scope.
 *
 * @author Rossitsa Borissova
 */
public class Scope implements Serializable {

    public static final String DESCRIPTION_FIELD = "description";
    public static final String CC_EXPIRES_IN_FIELD = "ccExpiresIn";
    public static final String PASS_EXPIRES_IN_FIELD = "passExpiresIn";
    public static final String REFRESH_EXPIRES_IN_FIELD = "refreshExpiresIn";

    public static final String JSON_CC_EXPIRES_IN = "cc_expires_in";
    public static final String JSON_PASS_EXPIRES_IN = "pass_expires_in";
    public static final String JSON_REFRESH_EXPIRES_IN = "refresh_expires_in";

    public static final Pattern SCOPE_PATTERN = Pattern.compile("^(\\p{Alnum}+-?_?)+$");

    @JSONField(ordinal = 1)
    private String scope;

    @JSONField(ordinal = 2)
    private String description;

    @JSONField(name = "cc_expires_in", ordinal = 3)
    private Integer ccExpiresIn;

    @JSONField(name = "pass_expires_in", ordinal = 4)
    private Integer passExpiresIn;

    @JSONField(name = "refresh_expires_in", ordinal = 5)
    private Integer refreshExpiresIn;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCcExpiresIn() {
        return ccExpiresIn;
    }

    public void setCcExpiresIn(Integer ccExpiresIn) {
        this.ccExpiresIn = ccExpiresIn;
    }

    public Integer getPassExpiresIn() {
        return passExpiresIn;
    }

    public void setPassExpiresIn(int passExpiresIn) {
        this.passExpiresIn = passExpiresIn;
    }

    public Integer getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(Integer refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public static Scope loadFromMap(Map<String, Object> map) {
        Scope scope = new Scope();
        scope.scope = (String) map.get("_id");
        scope.description = (String) map.get(DESCRIPTION_FIELD);
        scope.ccExpiresIn = (Integer) map.get(CC_EXPIRES_IN_FIELD);
        scope.passExpiresIn = (Integer) map.get(PASS_EXPIRES_IN_FIELD);
        scope.refreshExpiresIn = (Integer) ((map.get(REFRESH_EXPIRES_IN_FIELD) != null) ? map.get(REFRESH_EXPIRES_IN_FIELD) : map.get(PASS_EXPIRES_IN_FIELD));
        return scope;
    }

    public static Scope loadFromStringMap(Map<String, String> map) {
        Scope scope = new Scope();
        scope.scope = map.get("id");
        scope.description = map.get(DESCRIPTION_FIELD);
        scope.ccExpiresIn = Integer.valueOf(map.get(CC_EXPIRES_IN_FIELD));
        scope.passExpiresIn = Integer.valueOf(map.get(PASS_EXPIRES_IN_FIELD));
        String refreshExpiresIn = (map.get(REFRESH_EXPIRES_IN_FIELD) != null && map.get(REFRESH_EXPIRES_IN_FIELD).length() > 0) ? map.get(REFRESH_EXPIRES_IN_FIELD) : map.get(PASS_EXPIRES_IN_FIELD);
        scope.refreshExpiresIn = Integer.valueOf(refreshExpiresIn);
        return scope;
    }

    public static Scope loadFromStringList(List<String> list) {
        Scope scope = new Scope();
        scope.scope = list.get(0);
        scope.description = list.get(1);
        scope.ccExpiresIn = Integer.valueOf(list.get(2));
        scope.passExpiresIn = Integer.valueOf(list.get(3));
        String refreshExpiresIn = (list.get(4) != null && list.get(4).length() > 0) ? list.get(4) : list.get(3);
        scope.refreshExpiresIn = Integer.valueOf(refreshExpiresIn);
        return scope;
    }

    public boolean valid() {
        boolean validScope = scope != null && scope.length() >= 2;
        boolean validCCExpiresIn = ccExpiresIn != null && ccExpiresIn > 0;
        boolean validPassExpiresIn = passExpiresIn != null && passExpiresIn > 0;
        // in case refreshExpiresIn is not set, use passExpiresIn
        if (refreshExpiresIn == null || refreshExpiresIn < 0) {
            refreshExpiresIn = passExpiresIn;
        }
        boolean validRefreshExpiresIn = refreshExpiresIn != null && refreshExpiresIn > 0;
        if (!validScope || description == null || !validCCExpiresIn || !validPassExpiresIn || !validRefreshExpiresIn) {
            return false;
        }
        return true;
    }

    public boolean validForUpdate() {
        boolean valid = false;
        boolean validCCExpiresIn = ccExpiresIn != null && ccExpiresIn > 0;
        boolean validPassExpiresIn = passExpiresIn != null && passExpiresIn > 0;
        boolean validRefreshExpiresIn = refreshExpiresIn != null && refreshExpiresIn > 0;
        if ((description != null && description.length() > 0) || validCCExpiresIn || validPassExpiresIn || validRefreshExpiresIn) {
            valid = true;
        }
        return valid;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static boolean validScopeName(String scopeName) {
        Matcher m = SCOPE_PATTERN.matcher(scopeName);
        return m.find();
    }

    public static void main(String... args) {
        System.out.println(validScopeName("dao_getUserInfoDb"));
    }
}
