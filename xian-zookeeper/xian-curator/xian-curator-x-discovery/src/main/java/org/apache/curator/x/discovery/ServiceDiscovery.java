/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.curator.x.discovery;

import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import java.io.Closeable;
import java.util.Collection;
import java.util.List;

public interface ServiceDiscovery<T> extends Closeable
{
    /**
     * The discovery must be started before use
     *
     * @throws Exception errors
     */
    public void start() throws Exception;

    /**
     * Register/re-register a group
     *
     * @param service group to add
     * @throws Exception errors
     */
    public void     registerService(ServiceInstance<T> service) throws Exception;

    /**
     * Update a group
     *
     * @param service group to update
     * @throws Exception errors
     */
    public void     updateService(ServiceInstance<T> service) throws Exception;

    /**
     * Unregister/remove a group instance
     *
     * @param service the group
     * @throws Exception errors
     */
    public void     unregisterService(ServiceInstance<T> service) throws Exception;

    /**
     * Allocate a new group cache builder. The refresh padding is defaulted to 1 second.
     *
     * @return new cache builder
     */
    public ServiceCacheBuilder<T> serviceCacheBuilder();

    /**
     * Return the names of all known services
     *
     * @return list of group names
     * @throws Exception errors
     */
    public List<String> queryForNames() throws Exception;

    /**
     * Return all known instances for the given group
     *
     * @param name name of the group
     * @return list of instances (or an empty list)
     * @throws Exception errors
     */
    public Collection<ServiceInstance<T>>  queryForInstances(String name) throws Exception;

    /**
     * Return a group instance POJO
     *
     * @param name name of the group
     * @param id ID of the instance
     * @return the instance or <code>null</code> if not found
     * @throws Exception errors
     */
    public ServiceInstance<T> queryForInstance(String name, String id) throws Exception;

    /**
     * Allocate a new builder. {@link ServiceProviderBuilder#providerStrategy} is set to {@link RoundRobinStrategy}
     *
     * @return the builder
     */
    public ServiceProviderBuilder<T> serviceProviderBuilder();
}
