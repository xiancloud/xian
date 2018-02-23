/*
 * Copyright 2013-2015, ApiFest project
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
package com.apifest.oauth20.validator;

import com.apifest.oauth20.utils.ResponseBuilder;
import com.apifest.oauth20.bean.OAuthException;
import com.apifest.oauth20.bean.Scope;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Validates an input for scope services.
 *
 * @author Rossitsa Borissova
 *
 */
public class ScopeValidator implements JsonInputValidator {

    protected static ScopeValidator instance;

    private ScopeValidator() {
    }

    public void validate(String name, String value) throws OAuthException {
        if (Scope.JSON_CC_EXPIRES_IN.equals(name) || Scope.JSON_PASS_EXPIRES_IN.equals(name) || Scope.JSON_REFRESH_EXPIRES_IN.equals(name)) {
            try {
                Integer.valueOf(value);
            } catch (NumberFormatException e) {
                throw new OAuthException(String.format(ResponseBuilder.ERROR_NOT_INTEGER, name), HttpResponseStatus.BAD_REQUEST);
            }
        }
    }

    public static ScopeValidator getInstance() {
        return Holder.validator;
    }

    protected static class Holder {
        public static ScopeValidator validator = new ScopeValidator();

        // used for unit tests only!
        protected static void recreateInstance() {
            validator = new ScopeValidator();
        }
    }
}
