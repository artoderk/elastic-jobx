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

package com.dangdang.ddframe.job.internal.settings;

import com.dangdang.ddframe.job.api.GlobalSettingsAPI;
import com.dangdang.ddframe.job.domain.GlobalConfig;
import com.dangdang.ddframe.job.domain.GlobalStrategy;
import com.dangdang.ddframe.job.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.internal.storage.global.GlobalNodePath;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局配置的实现类.
 *
 * @author xiong.j
 */
@RequiredArgsConstructor
public final class GlobalSettingsAPIImpl implements GlobalSettingsAPI {
    
    private final CoordinatorRegistryCenter registryCenter;

	@Override
	public GlobalConfig getGlobalConfigs() {
        GlobalConfig result = new GlobalConfig();
        result.setSkipTimeStart(registryCenter.get(GlobalNodePath.SKIP_TIME_START));
        result.setSkipTimeEnd(registryCenter.get(GlobalNodePath.SKIP_TIME_END));
        if (registryCenter.isExisted(GlobalNodePath.HISTORY)) {
            result.setTriggerHistory(Boolean.valueOf(registryCenter.get(GlobalNodePath.HISTORY)));
        }
		return result;
	}

	@Override
	public List<GlobalStrategy> getGlobalStrategies() {
        List<GlobalStrategy> result = new ArrayList<GlobalStrategy>();
        
        int n = 1;
        List<String> list = registryCenter.getChildrenKeys(GlobalNodePath.STRATEGY);
        for (String val : list) {
            GlobalStrategy strategy = new GlobalStrategy();
            strategy.setName(getStrategyName(val));
            strategy.setPath(val);
            strategy.setNo(n);
            result.add(strategy);
            n++;
        }
		return result;
	}

	@Override
	public void updateGlobalConfigs(final GlobalConfig globalConfig) {
        fillNode(GlobalNodePath.SKIP_TIME_START, globalConfig.getSkipTimeStart());
        fillNode(GlobalNodePath.SKIP_TIME_END, globalConfig.getSkipTimeEnd());
        fillNode(GlobalNodePath.HISTORY, globalConfig.isTriggerHistory());
	}

	@Override
	public void updateGlobalStrategy(final GlobalStrategy globalStrategy) {
		int result = fillNode(GlobalNodePath.getStrategyNodePath(globalStrategy.getPath()), globalStrategy.getContent());

		// 如果是更新，则需要将所有用到该策略的任务重新分片
		if (result == 1) {
			List<String> jobNames = registryCenter.getChildrenKeys("/");
			for (String each : jobNames) {
                // 去除全局配置和锁
				if (each.equals(GlobalNodePath.GLOBAL) || each.equals("latch")) {
					continue;
				}
				JobNodePath jobNodePath = new JobNodePath(each);
				String jobShardingStrategyClass = registryCenter.get(jobNodePath.getConfigNodePath("jobShardingStrategyClass"));
				// 如果有任务用到该策略，需要使用新策略重新分片
				if (globalStrategy.getPath().equals(jobShardingStrategyClass)) {
					setReshardingFlag(jobNodePath);
				}
			}
		}
	}

	@Override
	public void removeGlobalStrategy(final String path) {
		if (null == path) return;
		// 注意，删除全局策略后，原来用到该全局策略的任务不会重新分片，直到触发分片时会使用默认的策略(除非修改策略路径)
        registryCenter.remove(GlobalNodePath.getStrategyNodePath(path));
	}

    /**
     * 填充节点，存在则替换，不存在则持久化.
     *
     * @param nodePath 作业节点路径
     * @param value 值
     */
    private int fillNode(final String nodePath, final Object value){
        if (null == value || value.toString().equals(registryCenter.get(nodePath))) {
            return 0;
        }
        if (isJobNodeExisted(nodePath)) {
            registryCenter.update(nodePath, value.toString());
            return 1;
        } else {
            registryCenter.persist(nodePath, value.toString());
            return 2;
        }
    }

    /**
     * 判断作业节点是否存在.
     *
     * @param nodePath 作业节点路径
     * @return 作业节点是否存在
     */
    private boolean isJobNodeExisted(final String nodePath) {
        return registryCenter.isExisted(nodePath);
    }

    /**
     * 根据策略类路径获取类名.
     *
     * @param path 策略类路径
     * @return 类名
     */
    private String getStrategyName(final String path){
    	if (path == null) return null;
    	return path.substring(path.lastIndexOf(".") + 1);
    }
    
    /**
     * 设置需要重新分片的标记.
     * 
     * @param jobNodePath
     */
    private void setReshardingFlag(final JobNodePath jobNodePath) {
    	String reshartdingFlagPath = jobNodePath.getFullPath("leader/sharding/necessary");
    	if (!isJobNodeExisted(reshartdingFlagPath)) {
    		registryCenter.persist(reshartdingFlagPath, "");
    	}
    }
    
	
//	public int updateGlobalStrategy(final GlobalStrategy globalStrategy) {
//        int flag = 0;
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(globalStrategy.getFile());
//
//            StringBuffer sb = new StringBuffer(4);
//            byte[] bbuf = new byte[4096];
//            int hasRead = 0;
//            while ((hasRead = fis.read(bbuf)) > 0) {
//                sb.append(new String(bbuf, 0, hasRead, "UTF-8"));
//            }
//            fillNode(GlobalNodePath.getStrategyNodePath(globalStrategy.getName()), sb.toString());
//
//            flag = 1;
//        } catch (FileNotFoundException e) {
//            log.error("保存全局分片策略失败", e);
//        } catch (IOException e) {
//            log.error("保存全局分片策略失败", e);
//        } finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    //
//                }
//            }
//        }
//        return flag;
//    }
}
