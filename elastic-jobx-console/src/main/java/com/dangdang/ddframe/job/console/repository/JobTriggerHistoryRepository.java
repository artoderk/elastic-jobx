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
     * @param jobTriggerHistory 作业执行记录
     * @return 记录数
     */
    int add(JobTriggerHistory jobTriggerHistory);

    /**
     * 修改作业执行记录
     * @param jobTriggerHistory 作业执行记录
     * @return 记录数
     */
    int update(JobTriggerHistory jobTriggerHistory);

    /**
     * 修改作业执行记录
     * @param jobTriggerHistory 作业执行记录
     * @return 记录数
     */
    int updateCompleteInfo(JobTriggerHistory jobTriggerHistory);

    /**
     * 统计作业执行记录
     * @param jobTriggerHistory 作业执行记录
     * @return 记录数
     */
    int count(JobTriggerHistory jobTriggerHistory);
    /**
     * 获取作业执行记录列表
     * @param jobTriggerHistory 作业执行记录
     * @return 作业执行记录列表
     */
    List<JobTriggerHistory> list(JobTriggerHistory jobTriggerHistory);

    /**
     * 获取作业执行记录
     * @param namespace 作业执行记录
     * @param jobName 作业名
     * @return
     */
    List<JobTriggerHistory> get(@Param("namespace")String namespace, @Param("jobName")String jobName);

    /**
     * 删除作业执行记录
     * @param jobTriggerHistory 作业执行记录
     * @return 记录数
     */
    int del(JobTriggerHistory jobTriggerHistory);
}
