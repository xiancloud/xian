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

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.InputStream;

/**
 * Validates a given input.
 *
 * @author Rossitsa Borissova
 */
public class InputValidator {

    /**
     * Validates an input and returns an instance of a given class constructed from the input.
     *
     * @param input the input to be validated
     * @param clazz the class to be used to create an instance from the input
     * @return an instance created from the input
     * @throws IOException ioException
     */
    public static <T> T validate(InputStream input, final Class<T> clazz) throws IOException {
        return JSON.parseObject(input, clazz);
    }

    public static <T> T validate(byte[] input, final Class<T> clazz) {
        return JSON.parseObject(input, clazz);
    }

    public static <T> T validate(String input, final Class<T> clazz) {
        return JSON.parseObject(input, clazz);
    }

}
