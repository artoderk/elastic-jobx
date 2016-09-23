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

import java.sql.Timestamp;
import java.util.List;

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
            log.warn("Duplicate insert, ignoring it. cause:" + e.getCause().getMessage());
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
    public List<JobTriggerHistory> list(JobTriggerHistory jobTriggerHistory) {
        if (jobTriggerHistory != null) {
            // TODO 分页
            List<JobTriggerHistory> list = repository.list(jobTriggerHistory);
            if (list != null && list.size() > 0) {
                for (JobTriggerHistory history : list) {
                    if (history.getStatus() > -1) {
                        history.setStatusValue(ExecutionInfo.ExecutionStatus.getEnum(history.getStatus()).getMemo());
                    }
                }
                return list;
            }
        }
        return null;
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

    private JobTriggerHistory getTriggerBeginInfo(final CoordinatorRegistryCenter registryCenter, String path) {
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
        result.setStatus(ExecutionInfo.ExecutionStatus.RUNNING.getCode());
//        if (registryCenter.isExisted(getFullPath(path, "/completed"))) {
//            result.setStatus(getCompleteFlag(registryCenter, path).getCode());
//            String lastCompleteTime = registryCenter.get(getFullPath(path, "/lastCompleteTime"));
//            result.setCompleteTime(null == lastCompleteTime ? null : new Timestamp(Long.parseLong(lastCompleteTime)));
//        }
        return result;
    }

    private JobTriggerHistory getTriggerCompleteInfo(final CoordinatorRegistryCenter registryCenter, String path) {
        path = path.substring(0, path.lastIndexOf("/"));
        JobTriggerHistory result = new JobTriggerHistory();
        setCommonItem(registryCenter, result, path);
        result.setStatus(getCompleteFlag(registryCenter, path).getCode());
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

    private void setCommonItem(final CoordinatorRegistryCenter registryCenter, JobTriggerHistory history, String path){
        history.setNamespace(((CuratorFramework)registryCenter.getRawClient()).getNamespace());
        String newPath = path.substring(1);
        history.setJobName(newPath.substring(0, newPath.indexOf("/")));
        history.setShardingItem(Integer.parseInt(path.substring(path.lastIndexOf("/") + 1)));
    }

    private ExecutionInfo.ExecutionStatus getCompleteFlag(final CoordinatorRegistryCenter registryCenter, final String path) {
        boolean completeFlag;
        String completedPath = getFullPath(path, "/completed");
        String completedValue = registryCenter.get(completedPath);
        if (Strings.isNullOrEmpty(completedValue)) {
            // 兼容老版本"/completed"无值的情况
            completeFlag = true;
        } else {
            completeFlag = Boolean.valueOf(completedValue);
        }
        if (completeFlag) {
            return ExecutionInfo.ExecutionStatus.COMPLETED;
        } else {
            return ExecutionInfo.ExecutionStatus.FAILED;
        }
    }
}
