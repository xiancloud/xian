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

package com.apifest.oauth20.api;

import java.util.Map;

/**
 * Represents user details associated with an access token.
 *
 * @author Rossitsa Borissova
 */
public class UserDetails {

    String userId;
    Map<String, String> details;

    public UserDetails(String userId, Map<String, String> details) {
        this.userId = userId;
        this.details = details;
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, String> getDetails() {
        return details;
    }

}
