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

package com.dangdang.ddframe.job.internal.storage;

import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * 命名空间节点数据访问类.
 *
 * @author xiong.j
 */
@RequiredArgsConstructor
public class NamespaceNodeStorage {

    private final CoordinatorRegistryCenter coordinatorRegistryCenter;

    /**
     * 判断节点是否存在.
     *
     * @param node 节点名称
     * @return 节点是否存在
     */
    public boolean isNodeExisted(final String node) {
        return coordinatorRegistryCenter.isExisted(node);
    }

    /**
     * 获取节点数据.
     *
     * @param node 作业节点名称
     * @return 作业节点数据值
     */
    public String getNodeData(final String node) {
        return coordinatorRegistryCenter.get(node);
    }

    /**
     * 注册连接状态监听器.
     */
    public void addConnectionStateListener(final ConnectionStateListener listener) {
        getClient().getConnectionStateListenable().addListener(listener);
    }

    /**
     * 注册数据监听器.
     *
     * @param listener 监听器
     */
    public void addDataListener(final TreeCacheListener listener) {
        TreeCache cache = (TreeCache) coordinatorRegistryCenter.getRawCache("/");
        cache.getListenable().addListener(listener);
    }

    /**
     * 移除数据监听器.
     *
     * @param listener 监听器
     */
    public void removeDataListener(final TreeCacheListener listener) {
        TreeCache cache = (TreeCache) coordinatorRegistryCenter.getRawCache("/");
        cache.getListenable().removeListener(listener);
    }

    private CuratorFramework getClient() {
        return (CuratorFramework) coordinatorRegistryCenter.getRawClient();
    }
    
}
