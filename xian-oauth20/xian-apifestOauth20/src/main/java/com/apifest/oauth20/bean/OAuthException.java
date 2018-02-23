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

import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom exception class.
 *
 * @author Rossitsa Borissova
 */
public class OAuthException extends Exception {

    private static final long serialVersionUID = -2288029087691423012L;

    protected static Logger log = LoggerFactory.getLogger(OAuthException.class);

    private String message;
    private HttpResponseStatus httpStatus;

    public OAuthException(String message, HttpResponseStatus httpStatus) {
        super();
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public OAuthException(Throwable e, String message, HttpResponseStatus httpStatus) {
        super(e);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpResponseStatus getHttpStatus() {
        return httpStatus;
    }

}
