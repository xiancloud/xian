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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Map;

/**
 * Utility class for JSON transformations.
 *
 * @author Rossitsa Borissova
 */
public final class JsonUtils {

    public static String convertMapToJSON(Map<String, String> list) {
        return JSON.toJSONString(list);
    }

    public static Map<String, String> convertStringToMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, String>>() {
        });
    }

}
