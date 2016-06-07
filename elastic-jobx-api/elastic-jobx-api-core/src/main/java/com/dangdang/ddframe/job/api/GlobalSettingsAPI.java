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

package com.dangdang.ddframe.job.api;

import java.util.List;

import com.dangdang.ddframe.job.domain.GlobalConfig;
import com.dangdang.ddframe.job.domain.GlobalStrategy;

/**
 * 全局设置配置的API.
 *
 * @author xiong.j
 */
public interface GlobalSettingsAPI {
    
    /**
     * 获取全局设置.
     *
     * @return 全局设置
     */
    GlobalConfig getGlobalConfigs();

    /**
     * 获取所有全局分片策略.
     *
     * @return 全局分片策略
     */
    List<GlobalStrategy> getGlobalStrategies();

    /**
     * 更新全局配置.
     *
     * @param globalConfig 全局配置
     */
    void updateGlobalConfigs(GlobalConfig globalConfig);

    /**
     * 更新全局分片策略.
     *
     * @param globalStrategy 全局分片策略
     */
    void updateGlobalStrategy(GlobalStrategy globalStrategy);

    /**
     * 删除全局分片策略.
     *
     * @param name 全局分片策略名
     */
    void removeGlobalStrategy(String name);
}
