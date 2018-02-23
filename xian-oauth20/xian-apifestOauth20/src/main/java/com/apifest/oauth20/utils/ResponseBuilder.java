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

import com.apifest.oauth20.bean.OAuthException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * Contains all supported responses and response messages.
 *
 * @author Rossitsa Borissova
 */
public final class ResponseBuilder {
    public static final String CANNOT_REGISTER_APP = "{\"error\": \"cannot issue client_id and client_secret\"}";
    public static final String NAME_OR_SCOPE_OR_URI_IS_NULL = "{\"error\": \"name, scope or redirect_uri is missing or invalid\"}";
    public static final String SCOPE_NOT_EXIST = "{\"error\": \"scope does not exist\"}";
    public static final String INVALID_CLIENT_ID = "{\"error\": \"invalid client_id/client_secret\"}";
    public static final String INVALID_CLIENT_CREDENTIALS = "{\"error\": \"invalid client_id/client_secret\"}";
    public static final String RESPONSE_TYPE_NOT_SUPPORTED = "{\"error\": \"unsupported_response_type\"}";
    public static final String INVALID_REDIRECT_URI = "{\"error\": \"invalid redirect_uri\"}";
    public static final String MANDATORY_PARAM_MISSING = "{\"error\": \"mandatory parameter %s is missing\"}";
    public static final String CANNOT_ISSUE_TOKEN = "{\"error\": \"cannot issue token\"}";
    public static final String INVALID_AUTH_CODE = "{\"error\": \"invalid auth_code\"}";
    public static final String GRANT_TYPE_NOT_SUPPORTED = "{\"error\": \"unsupported_grant_type\"}";
    public static final String INVALID_ACCESS_TOKEN = "{\"error\":\"invalid access token\"}";
    public static final String INVALID_REFRESH_TOKEN = "{\"error\":\"invalid refresh token\"}";
    public static final String INVALID_USERNAME_PASSWORD = "{\"error\": \"invalid username/password\"}";
    public static final String CANNOT_AUTHENTICATE_USER = "{\"error\": \"cannot authenticate user\"}";
    public static final String NOT_FOUND_CONTENT = "{\"error\":\"Not found\"}";
    public static final String UNSUPPORTED_MEDIA_TYPE = "{\"error\":\"unsupported media type\"}";
    public static final String CANNOT_UPDATE_APP = "{\"error\": \"cannot update client application\"}";
    public static final String UPDATE_APP_MANDATORY_PARAM_MISSING = "{\"error\": \"scope, description or status is missing or invalid\"}";
    public static final String ALREADY_REGISTERED_APP = "{\"error\": \"already registered client application\"}";
    public static final String CLIENT_APP_NOT_EXIST = "{\"error\": \"client application does not exist\"}";
    public static final String SCOPE_NOK_MESSAGE = "{\"status\":\"scope not valid\"}";
    public static final String CLIENT_APP_UPDATED = "{\"status\":\"client application updated\"}";
    public static final String CANNOT_LIST_CLIENT_APPS = "{\"error\":\"cannot list client applications\"}";
    public static final String INVALID_JSON_ERROR = "{\"error\":\"invalid JSON\"}";
    public static final String ERROR_NOT_INTEGER = "{\"error\":\"%s is not an integer\"}";

    public static final String APPLICATION_JSON = "application/json";


    public static FullHttpResponse createBadRequestResponse() {
        return createBadRequestResponse(null);
    }

    public static FullHttpResponse createBadRequestResponse(String message) {
        return createResponse(HttpResponseStatus.BAD_REQUEST, message);
    }

    public static FullHttpResponse createNotFoundResponse() {
        return createResponse(HttpResponseStatus.NOT_FOUND, ResponseBuilder.NOT_FOUND_CONTENT);
    }

    public static FullHttpResponse createOkResponse(String jsonString) {
        return createResponse(HttpResponseStatus.OK, jsonString);
    }

    public static FullHttpResponse createOAuthExceptionResponse(OAuthException ex) {
        return createResponse(ex.getHttpStatus(), ex.getMessage());
    }

    public static FullHttpResponse createUnauthorizedResponse() {
        return createResponse(HttpResponseStatus.UNAUTHORIZED, ResponseBuilder.INVALID_ACCESS_TOKEN);
    }

    public static FullHttpResponse createResponse(HttpResponseStatus status, String message) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        if (message != null) {
            ByteBuf buf = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
            response = response.replace(buf);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.writerIndex());
        } else {
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, APPLICATION_JSON);
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_STORE);
        response.headers().set(HttpHeaderNames.PRAGMA, HttpHeaderValues.NO_CACHE);
        return response;
    }
}

