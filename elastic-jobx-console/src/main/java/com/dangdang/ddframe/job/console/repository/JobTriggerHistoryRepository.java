package com.dangdang.ddframe.job.console.repository;

import com.dangdang.ddframe.job.console.domain.JobTriggerHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by xiong.j on 2016/9/2.
 */
public interface JobTriggerHistoryRepository {

    /**
     * 增加作业执行记录
     * @param jobTriggerHistory
     * @return
     */
    int add(JobTriggerHistory jobTriggerHistory);

    /**
     * 修改作业执行记录
     * @param jobTriggerHistory
     * @return
     */
    int update(JobTriggerHistory jobTriggerHistory);

    /**
     * 修改作业执行记录
     * @param jobTriggerHistory
     * @return
     */
    int updateCompleteInfo(JobTriggerHistory jobTriggerHistory);

    /**
     * 获取作业执行记录列表
     * @param jobTriggerHistory
     * @return
     */
    List<JobTriggerHistory> list(JobTriggerHistory jobTriggerHistory);

    /**
     * 获取作业执行记录
     * @param namespace
     * @param jobName
     * @return
     */
    List<JobTriggerHistory> get(@Param("namespace")String namespace, @Param("jobName")String jobName);

    /**
     * 删除作业执行记录
     * @param jobTriggerHistory
     * @return
     */
    int del(JobTriggerHistory jobTriggerHistory);
}
