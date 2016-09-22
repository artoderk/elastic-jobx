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

package com.dangdang.ddframe.job.console.service;

import com.dangdang.ddframe.job.console.domain.JobTriggerHistory;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;

import java.util.List;

/**
 * 操作执行历史的API.
 *
 * @author xiong.j
 */
public interface JobTriggerHistoryService {

    /**
     * 增加作业触发历史
     *
     * @param registryCenter 注册中心
     * @param path 路径
     * @return 记录数
     */
    int add(CoordinatorRegistryCenter registryCenter, String path);

    /**
     * 修改作业触发历史
     *
     * @param path 路径
     * @return 记录数
     */
    int update(CoordinatorRegistryCenter registryCenter, String path);

    /**
     * 获取作业执行记录列表
     * @param jobTriggerHistory 作业执行记录
     * @return 作业执行记录列表
     */
    List<JobTriggerHistory> list(JobTriggerHistory jobTriggerHistory);

    /**
     * 作业是否开始
     *
     * @param path 作业路径
     * @return 作业是否开始
     */
    boolean isBegin(String path);

    /**
     * 作业是否结束
     *
     * @param path 作业路径
     * @return 作业是否结束
     */
    boolean isComplete(String path);
}
