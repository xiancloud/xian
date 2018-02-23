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

package com.apifest.oauth20.conf;

import com.apifest.oauth20.api.ICustomGrantTypeHandler;
import com.apifest.oauth20.api.IUserAuthentication;
import com.apifest.oauth20.persistence.DBManagerFactory;
import com.apifest.oauth20.LifecycleEventHandlers;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.TraverseClasspath;

import java.util.Set;

/**
 * Class responsible for ApiFest OAuth 2.0 Server config.
 *
 * @author Rossitsa Borissova and modified by happyyangyuan
 */
public final class OAuthConfig {

    private static Class<? extends IUserAuthentication> userAuthenticationClass;
    private static String customGrantType;
    private static Class<? extends ICustomGrantTypeHandler> customGrantTypeHandler;

    // expires_in in sec for grant type password
    public static final int DEFAULT_PASSWORD_EXPIRES_IN = 900;

    // expires_in in sec for grant type client_credentials
    public static final int DEFAULT_CC_EXPIRES_IN = 1800;

    private OAuthConfig() {
    }

    public static void main(String[] args) {
        if (!loadConfig()) {
            System.exit(1);
        }
        DBManagerFactory.init();
    }

    protected static boolean loadConfig() {
        boolean loaded = loadProperties();

        userAuthenticationClass = loadCustomUserAuthentication();

        if (customGrantType != null && customGrantType.length() > 0) {
            customGrantTypeHandler = loadCustomGrantTypeClass();
        }

        LifecycleEventHandlers.loadLifecycleHandlers();

        return loaded;
    }

    private static final Object lock = new Object();

    private static Class<? extends IUserAuthentication> loadCustomUserAuthentication() {
        if (userAuthenticationClass == null) {
            synchronized (lock) {
                if (userAuthenticationClass == null) {
                    Set<Class<? extends IUserAuthentication>> classes = TraverseClasspath.getSubclasses(IUserAuthentication.class);
                    if (classes == null || classes.isEmpty()) {
                        LOG.warn("No IUserAuthentication implementations found! User authentication will always pass successfully", new Exception());
                    } else {
                        if (classes.size() > 1) {
                            LOG.warn(String.format("{%s} IUserAuthentication implementation classes found! Only the first one {%s} is used.", classes.size(), classes.iterator().next()), new Exception());
                        }
                        userAuthenticationClass = classes.iterator().next();
                    }
                }
            }
        }
        return userAuthenticationClass;
    }

    public static Class<? extends ICustomGrantTypeHandler> loadCustomGrantTypeClass() {
        if (customGrantTypeHandler == null) {
            synchronized (lock) {
                if (customGrantTypeHandler == null) {
                    Set<Class<? extends ICustomGrantTypeHandler>> classes = TraverseClasspath.getSubclasses(ICustomGrantTypeHandler.class);
                    if (classes == null || classes.isEmpty()) {
                        throw new RuntimeException(String.format("No ICustomGrantTypeHandler implementation found for custom.grant_type={%s}", customGrantType));
                    } else {
                        if (classes.size() > 1) {
                            LOG.warn(String.format("%s ICustomGrantTypeHandler implementations found!  Only the first : %s takes effect!", classes.size(), classes.iterator().next()));
                        }
                        customGrantTypeHandler = classes.iterator().next();
                    }
                }
            }
        }
        return customGrantTypeHandler;
    }

    protected static boolean loadProperties() {
        try {
            customGrantType = EnvConfig.get("custom.grant_type");
        } catch (Throwable e) {
            LOG.error(e);
            return false;
        }
        return true;
    }


    public static Class<? extends IUserAuthentication> getUserAuthenticationClass() {
        return userAuthenticationClass;
    }

    public static String getCustomGrantType() {
        return customGrantType;
    }

    public static Class<? extends ICustomGrantTypeHandler> getCustomGrantTypeHandler() {
        return customGrantTypeHandler;
    }

}
