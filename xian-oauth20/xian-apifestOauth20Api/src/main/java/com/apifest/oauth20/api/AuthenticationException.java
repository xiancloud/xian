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

import io.netty.handler.codec.http.HttpResponse;

/**
 * Exception thrown when something goes wrong with authentication.
 *
 * @author Rossitsa Borissova
 */
public class AuthenticationException extends Exception {

    private static final long serialVersionUID = -5776710386861918365L;

    private String message;

    // an HTTP response that should be returned as a result of issue access token
    // for instance, if the user authentication requires more user details
    private HttpResponse response;

    public AuthenticationException(String message) {
        this.message = message;
    }

    public AuthenticationException(HttpResponse response) {
        this.response = response;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Returns the HTTP response that should be returned.
     *
     * @return {@link HttpResponse} response
     */
    public HttpResponse getResponse() {
        return response;
    }

}
