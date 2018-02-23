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

/**
 * @author Apostol Terziev
 */
package com.apifest.oauth20.persistence;

import com.apifest.oauth20.persistence.redis.RedisDBManager;

public class DBManagerFactory {

    protected static volatile DBManager dbManager;

    public static final String REDIS_DB = "redis";

    private static final Object lock = new Object();
    //并发安全的
    public static DBManager getInstance() {
        if (dbManager == null) {
            synchronized (lock){
                if(dbManager == null){
                    dbManager = new RedisDBManager();
                }
            }
        }
        return dbManager;
    }

    public static void init() {
        // that will instantiate a connection to the storage
        getInstance();
    }
}
