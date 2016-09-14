package com.dangdang.ddframe.job.console.listener;

import com.dangdang.ddframe.job.console.service.JobTriggerHistoryService;
import com.dangdang.ddframe.job.console.zookeeper.ConsoleRegistryCenter;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * 作业命名空间观察者
 *
 * Created by xiong.j on 2016/9/2.
 */
@RequiredArgsConstructor
public class RegistryCenterObserver implements Observer {

    private final JobTriggerHistoryService jobTriggerHistoryService;

    private final ConsoleRegistryCenter registryCenter;

    private final List<JobTriggerManager> queue = new ArrayList<>();

    /**
     * 当新的命名空间加入时，初始化并启动作业执行状态监听器
     *
     * @param o 命名空间
     * @param arg 参数
     */
    @Override
    public synchronized void update(Observable o, Object arg) {
        JobTriggerManager jobTriggerManager = new JobTriggerManager((CoordinatorRegistryCenter)arg, registryCenter, jobTriggerHistoryService);
        if (registryCenter.hasLeadership()) {
            jobTriggerManager.start();
        }
        queue.add(jobTriggerManager);
    }

    /**
     * 启动作业执行状态监听器
     */
    public synchronized void start() {
        JobTriggerManager jobTriggerManager;
        for (Iterator<JobTriggerManager> iterator = queue.iterator(); iterator.hasNext();) {
            jobTriggerManager = iterator.next();
            jobTriggerManager.start();
        }
    }

    /**
     * 关闭作业执行状态监听器
     */
    public synchronized void close() {
        JobTriggerManager jobTriggerManager;
        for (Iterator<JobTriggerManager> iterator = queue.iterator(); iterator.hasNext();) {
            jobTriggerManager = iterator.next();
            jobTriggerManager.close();
        }
    }
}
