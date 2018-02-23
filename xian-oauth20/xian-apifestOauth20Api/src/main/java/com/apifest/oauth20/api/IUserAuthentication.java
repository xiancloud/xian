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

import io.netty.handler.codec.http.HttpRequest;


/**
 * Interface for user authentication.
 *
 * @author Rossitsa Borissova
 */
public interface IUserAuthentication {

    /**
     * Authenticates the user using username, password and what info required from the authentication request.
     * @param username username
     * @param password password
     * @param authRequest the authentication request
     * @return details about the authenticated user
     * @throws AuthenticationException
     */
    UserDetails authenticate(String username, String password, HttpRequest authRequest) throws AuthenticationException;

}
