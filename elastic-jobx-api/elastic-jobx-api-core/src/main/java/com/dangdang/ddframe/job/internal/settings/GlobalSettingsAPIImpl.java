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

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.dangdang.ddframe.job.api.GlobalSettingsAPI;
import com.dangdang.ddframe.job.domain.GlobalConfig;
import com.dangdang.ddframe.job.domain.GlobalStrategy;
import com.dangdang.ddframe.job.internal.storage.GlobalNodePath;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;

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
	}

	@Override
	public void updateGlobalStrategy(final GlobalStrategy globalStrategy) {
		fillNode(GlobalNodePath.getStrategyNodePath(globalStrategy.getPath()), globalStrategy.getContent());
	}
	
//	public int updateGlobalStrategy(final GlobalStrategy globalStrategy) {
//        int flag = 0;
//        FileInputStream fis = null;
//        try {
//        	// TODO test
//        	if (globalStrategy.getFile() == null) {
//        		globalStrategy.setFile(new File("D:\\Projects\\TestGroovy\\src\\groovy\\strategy"));
//        		globalStrategy.setName("TestDynamicStrategy");
//        	}
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

	@Override
	public void removeGlobalStrategy(final String name) {
        registryCenter.remove(GlobalNodePath.STRATEGY);
	}

    /**
     * 填充节点，存在则替换，不存在则持久化.
     *
     * @param nodePath 作业节点路径
     * @param value 值
     */
    private void fillNode(final String nodePath, final Object value){
        if (null == value || value.toString().equals(registryCenter.get(nodePath))) {
            return;
        }
        if (isJobNodeExisted(nodePath)) {
            registryCenter.update(nodePath, value.toString());
        } else {
            registryCenter.persist(nodePath, value.toString());
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
    private String getStrategyName(String path){
    	if (path == null) return null;
    	return path.substring(path.lastIndexOf(".") + 1);
    }
}
