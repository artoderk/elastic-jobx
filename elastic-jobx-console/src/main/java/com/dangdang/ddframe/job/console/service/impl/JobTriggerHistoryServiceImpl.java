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

package com.dangdang.ddframe.job.console.service.impl;

import com.dangdang.ddframe.job.console.domain.JobTriggerHistory;
import com.dangdang.ddframe.job.console.repository.JobTriggerHistoryRepository;
import com.dangdang.ddframe.job.console.service.JobTriggerHistoryService;
import com.dangdang.ddframe.job.domain.ExecutionInfo;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * 操作执行历史的实现类.
 *
 * @author xiong.j
 */
@Service
@Slf4j
public final class JobTriggerHistoryServiceImpl implements JobTriggerHistoryService {

    @Autowired
    private JobTriggerHistoryRepository repository;

    @Override
    public int add(CoordinatorRegistryCenter registryCenter, String path) {
        JobTriggerHistory jobTriggerHistory = getTriggerBeginInfo(registryCenter, path);
        log.debug("Job trigger history add. " + jobTriggerHistory);
        try {
            return repository.add(jobTriggerHistory);
        } catch (DuplicateKeyException e) {
            log.warn("Duplicate insert, ignoring it.", e);
        }
        return 0;
    }

    @Override
    public int update(CoordinatorRegistryCenter registryCenter, String path) {
        JobTriggerHistory jobTriggerHistory = getTriggerCompleteInfo(registryCenter, path);
        log.debug("Job trigger history update. " + jobTriggerHistory);
        return repository.updateCompleteInfo(jobTriggerHistory);
    }

    @Override
    public boolean isBegin(String path){
        if (Strings.isNullOrEmpty(path)) return false;
        return path.endsWith("lastBeginTime");
    }

    @Override
    public boolean isComplete(String path){
        if (Strings.isNullOrEmpty(path)) return false;
        return path.endsWith("lastCompleteTime");
    }

    private JobTriggerHistory getTriggerBeginInfo(CoordinatorRegistryCenter registryCenter, String path) {
        path = path.substring(0, path.lastIndexOf("/"));
        JobTriggerHistory result = new JobTriggerHistory();
        setCommonItem(registryCenter, result, path);
        result.setShardingCount(Integer.parseInt(registryCenter.get(getFullPath("/", result.getJobName(), "/config", "/shardingTotalCount"))));
        if (registryCenter.isExisted(getFullPath(path, "/failover"))) {
            result.setFailoverIp(registryCenter.get(getFullPath(path, "/failover")));
        }
        String lastBeginTime = registryCenter.get(getFullPath(path, "/lastBeginTime"));
        result.setBeginTime(null == lastBeginTime ? null : new Timestamp(Long.parseLong(lastBeginTime)));
        String nextFireTime = registryCenter.get(getFullPath(path, "/nextFireTime"));
        result.setNextFireTime(null == nextFireTime ? null : new Timestamp(Long.parseLong(nextFireTime)));
        if (registryCenter.isExisted(getFullPath(path, "/completed"))) {
            // TODO 执行失败未处理
            result.setStatus(ExecutionInfo.ExecutionStatus.COMPLETED.getCode());
            String lastCompleteTime = registryCenter.get(getFullPath(path, "/lastCompleteTime"));
            result.setCompleteTime(null == lastCompleteTime ? null : new Timestamp(Long.parseLong(lastCompleteTime)));
        }
        return result;
    }

    private JobTriggerHistory getTriggerCompleteInfo(CoordinatorRegistryCenter registryCenter, String path) {
        path = path.substring(0, path.lastIndexOf("/"));
        JobTriggerHistory result = new JobTriggerHistory();
        setCommonItem(registryCenter, result, path);
        // TODO 执行失败未处理
        result.setStatus(ExecutionInfo.ExecutionStatus.COMPLETED.getCode());
        String lastBeginTime = registryCenter.get(getFullPath(path, "/lastBeginTime"));
        result.setBeginTime(null == lastBeginTime ? null : new Timestamp(Long.parseLong(lastBeginTime)));
        String lastCompleteTime = registryCenter.get(getFullPath(path, "/lastCompleteTime"));
        result.setCompleteTime(null == lastCompleteTime ? null : new Timestamp(Long.parseLong(lastCompleteTime)));
        return result;
    }

    private String getFullPath(String... path){
        String result = "";
        for (String str : path) {
            result += str;
        }
        return result;
    }

    private void setCommonItem(CoordinatorRegistryCenter registryCenter, JobTriggerHistory history, String path){
        history.setNamespace(((CuratorFramework)registryCenter.getRawClient()).getNamespace());
        String newPath = path.substring(1);
        history.setJobName(newPath.substring(0, newPath.indexOf("/")));
        history.setShardingItem(Integer.parseInt(path.substring(path.lastIndexOf("/") + 1)));
    }

}
