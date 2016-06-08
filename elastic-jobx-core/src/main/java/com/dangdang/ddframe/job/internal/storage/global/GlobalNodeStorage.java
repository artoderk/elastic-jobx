/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.job.internal.storage.global;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;

/**
 * 全局配置数据访问类.
 * 
 * <p>
 * 全局配置节点是在注册中心节点的/global目录.
 * </p>
 * 
 * @author xiong.j
 */
public class GlobalNodeStorage {

    private final CoordinatorRegistryCenter coordinatorRegistryCenter;

    public GlobalNodeStorage(final CoordinatorRegistryCenter coordinatorRegistryCenter) {
        this.coordinatorRegistryCenter = coordinatorRegistryCenter;
    }

    /**
     * 判断作业节点是否存在.
     *
     * @param node 作业节点名称
     * @return 作业节点是否存在
     */
    public boolean isJobNodeExisted(final String node) {
        return coordinatorRegistryCenter.isExisted(node);
    }

    /**
     * 获取作业节点数据.
     *
     * @param node 作业节点名称
     * @return 作业节点数据值
     */
    public String getJobNodeData(final String node) {
        return coordinatorRegistryCenter.get(node);
    }

    /**
     * 直接从注册中心而非本地缓存获取作业节点数据.
     *
     * @param node 作业节点名称
     * @return 作业节点数据值
     */
    public String getJobNodeDataDirectly(final String node) {
        return coordinatorRegistryCenter.getDirectly(node);
    }

    /**
     * 获取作业节点子节点名称列表.
     *
     * @param node 作业节点名称
     * @return 作业节点子节点名称列表
     */
    public List<String> getJobNodeChildrenKeys(final String node) {
        return coordinatorRegistryCenter.getChildrenKeys(node);
    }

    /**
     * 注册连接状态监听器.
     *
     * @param listener 监听器
     */
    public void addConnectionStateListener(final ConnectionStateListener listener) {
        getClient().getConnectionStateListenable().addListener(listener);
    }

    private CuratorFramework getClient() {
        return (CuratorFramework) coordinatorRegistryCenter.getRawClient();
    }

    /**
     * 注册数据监听器.
     *
     * @param listener 监听器
     */
    public void addDataListener(final TreeCacheListener listener) {
        TreeCache cache = (TreeCache) coordinatorRegistryCenter.getRawCache(GlobalNodePath.ROOT);
        cache.getListenable().addListener(listener);
    }

}
