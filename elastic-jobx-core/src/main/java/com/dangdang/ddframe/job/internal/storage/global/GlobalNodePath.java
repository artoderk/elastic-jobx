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

import lombok.NoArgsConstructor;

/**
 * 作业节点路径类.
 * 
 * <p>
 * 全局配置节点是在注册中心节点的global目录.
 * </p>
 * 
 * @author xiong.j
 */
@NoArgsConstructor
public final class GlobalNodePath {
    /**
     * 根节点.
     */
    public static final String ROOT = "/global";
    
    /**
     * 根节点标识.
     */
    public static final String GLOBAL = "global";

    /**
     * 配置节点.
     */
    public static final String CONFIG = ROOT + "/config";

    /**
     * 作业跳过执行期间终点时间节点名称.
     */
    public static final String SKIP_TIME_START = CONFIG + "/skipStartTime";
    
    /**
     * 作业关闭节点名称.
     */
    public static final String SKIP_TIME_END = CONFIG + "/skipEndTime";
    
    /**
     * 作业分片策略实现类节点名称.
     */
    public static final String STRATEGY = ROOT + "/strategy";
    
    /**
     * 获取作业分片策略实现类节点路径.
     * 
     * @param nodeName 节点名称
     * @return 作业分片策略实现类节点路径
     */
    public static String getStrategyNodePath(final String nodeName) {
        return String.format("%s/%s", STRATEGY, nodeName);
    }

    /**
     * 判断是否为作业分片策略实现类节点路径.
     *
     * @param path 节点路径
     * @return 是否为作业分片策略实现类节点路径
     */
    public static boolean isShardingStrategyPath(final String path) {
        return path.indexOf(STRATEGY) == 0;
    }

}
