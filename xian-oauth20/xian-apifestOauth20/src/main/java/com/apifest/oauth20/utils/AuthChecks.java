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

package com.apifest.oauth20.utils;

import com.apifest.oauth20.persistence.DBManager;
import com.apifest.oauth20.persistence.DBManagerFactory;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;

/**
 * Performs authentication checks.
 *
 * @author Rossitsa Borissova
 */
public class AuthChecks {

    private static final String BASIC = "Basic ";

    protected DBManager db = DBManagerFactory.getInstance();

    protected String getBasicAuthenticationClientId(HttpRequest req) {
        // extract Basic Authentication header
        String authHeader = req.headers().get(HttpHeaders.AUTHORIZATION);
        String clientId = null;
        if (authHeader != null && authHeader.contains(BASIC)) {
            String value = authHeader.replace(BASIC, "");
            Base64 decoder = new Base64();
            byte[] decodedBytes = decoder.decode(value);
            String decoded = new String(decodedBytes);
            // client_id:client_secret
            String[] str = decoded.split(":");
            if (str.length == 2) {
                String authClientId = str[0];
                String authClientSecret = str[1];
                // check valid - DB call
                if (db.validClient(authClientId, authClientSecret).blockingGet()) {
                    clientId = authClientId;
                }
            }
        }
        return clientId;
    }
}
