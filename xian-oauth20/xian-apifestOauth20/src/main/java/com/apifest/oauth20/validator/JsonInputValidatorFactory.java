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

import com.apifest.oauth20.bean.ApplicationInfo;
import com.apifest.oauth20.bean.Scope;

/**
 * Factory class for different input validators.
 *
 * @author Rossitsa Borissova
 *
 */
public class JsonInputValidatorFactory {

    /**
     * Instantiates a validator depending on the class passed as a parameter.
     * @param clazz the class the input will be validated to
     * @return JsonInputValidator
     */
    public static JsonInputValidator getValidator(Class<?> clazz) {
        if (clazz.equals(Scope.class)) {
            return ScopeValidator.getInstance();
        }
        if (clazz.equals(ApplicationInfo.class)) {
            return ApplicationInfoValidator.getInstance();
        }
        return null;
    }
}
