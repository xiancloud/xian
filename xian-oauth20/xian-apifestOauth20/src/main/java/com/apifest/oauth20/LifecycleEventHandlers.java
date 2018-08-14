/*
 * Copyright 2014, ApiFest project
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

package com.apifest.oauth20;

import com.apifest.oauth20.api.*;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.TraverseClasspath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Loads lifecycle event handlers on OAuth server startup.
 *
 * @author Rossitsa Borissova
 */
public class LifecycleEventHandlers {

    private final static List<Class<? extends LifecycleHandler>> private_requestEventHandlers = new ArrayList<>();
    public final static List<Class<? extends LifecycleHandler>> requestEventHandlers = Collections.unmodifiableList(private_requestEventHandlers);
    private final static List<Class<? extends LifecycleHandler>> private_responseEventHandlers = new ArrayList<>();
    public final static List<Class<? extends LifecycleHandler>> responseEventHandlers = Collections.unmodifiableList(private_responseEventHandlers);
    private final static List<Class<? extends ExceptionEventHandler>> private_exceptionHandlers = new ArrayList<>();
    public static final List<Class<? extends ExceptionEventHandler>> exceptionHandlers = Collections.unmodifiableList(private_exceptionHandlers);
    private static final AtomicBoolean loadedLock = new AtomicBoolean(false);

    //懒加载,并发安全的
    public static void loadLifecycleHandlers() {
        if (!loadedLock.get())
            synchronized (loadedLock) {
                if (!loadedLock.get()) {
                    Set<Class<? extends LifecycleHandler>> subClasses = TraverseClasspath.getNonAbstractSubclasses(LifecycleHandler.class);
                    for (Class<? extends LifecycleHandler> clazz : subClasses) {
                        if (clazz.isAnnotationPresent(OnRequest.class)) {
                            private_requestEventHandlers.add(clazz);
                            LOG.info(String.format("preIssueTokenHandler added {%s}", clazz));
                        }
                        if (clazz.isAnnotationPresent(OnResponse.class)) {
                            private_responseEventHandlers.add(clazz);
                            LOG.info(String.format("postIssueTokenHandler added {%s}", clazz));
                        }
                    }
                    Set<Class<? extends ExceptionEventHandler>> exceptionHandlerClasses = TraverseClasspath.getNonAbstractSubclasses(ExceptionEventHandler.class);
                    for (Class<? extends ExceptionEventHandler> clazz : exceptionHandlerClasses) {
                        if (clazz.isAnnotationPresent(OnException.class)) {
                            private_exceptionHandlers.add(clazz);
                            LOG.info(String.format("exceptionHandlers added {%s}", clazz));
                        }
                    }
                    loadedLock.set(true);
                }
            }
    }

}
