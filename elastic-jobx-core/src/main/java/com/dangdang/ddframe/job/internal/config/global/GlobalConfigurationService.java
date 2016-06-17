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

package com.dangdang.ddframe.job.internal.config.global;

import java.util.Date;

import com.dangdang.ddframe.job.internal.storage.global.GlobalNodePath;
import com.dangdang.ddframe.job.internal.storage.global.GlobalNodeStorage;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.util.DateUtil;
import com.dangdang.ddframe.util.objectpool.HashObjectPool;

/**
 * 全局配置服务.
 * 
 * @author xiong.j
 */
public class GlobalConfigurationService {

	private static final HashObjectPool<CoordinatorRegistryCenter, GlobalConfigurationService> globalConfigClassPool = 
			new HashObjectPool<CoordinatorRegistryCenter, GlobalConfigurationService>();
	
    private final GlobalNodeStorage globalNodeStorage;
    
    private GlobalConfigurationService(final CoordinatorRegistryCenter coordinatorRegistryCenter) {
    	globalNodeStorage = new GlobalNodeStorage(coordinatorRegistryCenter);
    	coordinatorRegistryCenter.addCacheData(GlobalNodePath.ROOT);
    }
    
    /**
     * 获取全局配置实例
     * 
     * @param coordinatorRegistryCenter 注册中心
     * @return 全局配置实例
     */
    public static GlobalConfigurationService getInstance(final CoordinatorRegistryCenter coordinatorRegistryCenter) {
		if (null == coordinatorRegistryCenter) return null;

    	if (globalConfigClassPool.containsKey(coordinatorRegistryCenter)) {
    		return globalConfigClassPool.get(coordinatorRegistryCenter);
    	}
    	
    	synchronized(GlobalConfigurationService.class){
    		if (globalConfigClassPool.containsKey(coordinatorRegistryCenter)) {
        		return globalConfigClassPool.get(coordinatorRegistryCenter);
        	} else {
        		return new GlobalConfigurationService(coordinatorRegistryCenter);
        	}
    	}
    }
    
    /**
     * 获取全局配置数据访问类
     * 
     * @return 全局配置数据访问类
     */
    public GlobalNodeStorage getGlobalNodeStorage() {
    	return globalNodeStorage;
    }
    
    /**
     * 获取作业分片策略实现类.
     * 
     * @param nodeName 作业分片策略实现类名
     * @return 作业分片策略实现类
     */
    public String getJobShardingStrategy(String nodeName) {
    	return globalNodeStorage.getJobNodeData(GlobalNodePath.getStrategyNodePath(nodeName));
    }
    
    /**
     * 是否在跳过执行期间内.
     * @param now 当前时间
     *
     * @return true 在跳过执行期间内
     */
    public boolean inSkipTime(Date now) {
    	// 全局跳过执行期间
    	Date startDate = DateUtil.parseDate(globalNodeStorage.getJobNodeData(GlobalNodePath.SKIP_TIME_START));
    	Date endDate   = DateUtil.parseDate(globalNodeStorage.getJobNodeData(GlobalNodePath.SKIP_TIME_END));

    	return DateUtil.betweenDate(now, startDate, endDate);
    }
}
