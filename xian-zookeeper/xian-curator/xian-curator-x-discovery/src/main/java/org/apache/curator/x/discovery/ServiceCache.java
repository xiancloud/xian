/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.curator.x.discovery;

import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.x.discovery.details.InstanceProvider;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.io.Closeable;
import java.util.List;

/**
 * @author modified by happyyangyuan
 */
public interface ServiceCache<T> extends Closeable, Listenable<ServiceCacheListener>, InstanceProvider<T> {
    /**
     * Return the current list of instances. NOTE: there is no guarantee of freshness. This is
     * merely the last known list of instances. However, the list is updated via a ZooKeeper watcher
     * so it should be fresh within a window of a second or two.
     *
     * @return the list
     */
    List<ServiceInstance<T>> getInstances();

    /**
     * The cache must be started before use
     *
     * @throws Exception errors
     */
    void start() throws Exception;

    /**
     * @return 最新版的服务定义，取值存储于服务节点的data内，如果节点不存在或者节点data为空，那么返回null
     */
    T getNewestServiceDefinition();
}
