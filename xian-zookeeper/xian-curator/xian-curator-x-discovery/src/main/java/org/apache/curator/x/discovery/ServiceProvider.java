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

import org.apache.curator.x.discovery.details.InstanceProvider;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;

/**
 * The main API for Discovery. This class is essentially a facade over a {@link ProviderStrategy}
 * paired with an {@link InstanceProvider}
 */
public interface ServiceProvider<T> extends Closeable {
    /**
     * The provider must be started before use
     *
     * @throws Exception any errors
     */
    public void start() throws Exception;

    /**
     * Return an instance for a single use. <b>IMPORTANT: </b> users
     * should not hold on to the instance returned. They should always get a fresh instance.
     *
     * @return the instance to use
     * @throws Exception any errors
     */
    public ServiceInstance<T> getInstance() throws Exception;

    /**
     * Return the current available set of instances <b>IMPORTANT: </b> users
     * should not hold on to the instance returned. They should always get a fresh list.
     *
     * @return all known instances. If no instances found , An empty collection will be returned.
     * @throws Exception any errors
     */
    public List<ServiceInstance<T>> getAllInstances() throws Exception;

    /**
     * Take note of an error connecting to the given instance. The instance will potentially
     * be marked as "down" depending on the {@link DownInstancePolicy}.
     *
     * @param instance instance that had an error
     */
    public void noteError(ServiceInstance<T> instance);

    /**
     * 由歪歪扩展实现beyond lifecycle
     *
     * @return 存储于service节点data对应的最新服务定义
     */
    T getNewestServiceDefinition();

    /**
     * @return the getGroup cache singleton instance
     */
    ServiceCache<T> serviceCache();

    /**
     * @param id 实例id
     * @return 对应的实例，如果不存在id，那么返回null
     */
    ServiceInstance<T> getInstance(String id);


    /**
     * @return 负载均衡策略
     */
    ProviderStrategy<T> getProviderStrategy();

}
