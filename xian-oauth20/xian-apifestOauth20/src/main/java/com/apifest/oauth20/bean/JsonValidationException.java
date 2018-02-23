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
package com.apifest.oauth20.bean;

import com.alibaba.fastjson.JSONException;

/**
 * Represents a JSON validation exception.
 *
 * @author Rossitsa Borissova
 *
 */
public class JsonValidationException extends JSONException {

    private static final long serialVersionUID = 4343311202369477896L;

    public JsonValidationException(String message) {
        super(message, null);
    }
}
