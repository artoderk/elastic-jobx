package com.dangdang.ddframe.job.console.listener;

import com.dangdang.ddframe.job.console.service.JobTriggerHistoryService;
import com.dangdang.ddframe.job.console.zookeeper.ConsoleRegistryCenter;
import com.dangdang.ddframe.job.internal.listener.AbstractJobListener;
import com.dangdang.ddframe.job.internal.storage.NamespaceNodeStorage;
import com.dangdang.ddframe.job.internal.storage.global.GlobalNodePath;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

/**
 * 作业执行状态监听器
 *
 * Created by xiong.j on 2016/9/2.
 */
@Slf4j
public class JobTriggerManager {

    private final CoordinatorRegistryCenter jobRegistryCenter;

    private final ConsoleRegistryCenter consoleRegistryCenter;

    private final JobTriggerHistoryService jobTriggerHistoryService;

    private JobExecutorListener jobExecutorListener;

    private NamespaceNodeStorage namespaceNodeStorage;

    public JobTriggerManager(CoordinatorRegistryCenter jobRegistryCenter, ConsoleRegistryCenter consoleRegistryCenter, JobTriggerHistoryService jobTriggerHistoryService) {
        this.jobRegistryCenter = jobRegistryCenter;
        this.consoleRegistryCenter = consoleRegistryCenter;
        this.jobTriggerHistoryService = jobTriggerHistoryService;

        jobRegistryCenter.addCacheData("/");
        namespaceNodeStorage = new NamespaceNodeStorage(jobRegistryCenter);
        namespaceNodeStorage.addDataListener(new MonitorOptionChangedListener());
    }

    /**
     * 启动作业执行状态监听器
     */
    public void start() {
        if (isMonitorTriggerHistory() && consoleRegistryCenter.hasLeadership()) {
            startTrigger();
            log.info("Job trigger listener start. namespace:" + getNamespace());
        } else {
            log.info("start() method is ignored, because this server isn't leader. namespace:" + getNamespace());
        }
    }

    /**
     * 关闭作业执行状态监听器
     */
    public void close(){
        if (jobExecutorListener != null) {
            namespaceNodeStorage.removeDataListener(jobExecutorListener);
            log.info("Job trigger listener close. namespace:" + getNamespace());
        } else {
            log.info("close() method is ignored, because this server isn't leader. namespace:" + getNamespace());
        }
    }

    /**
     * 获取当前的命名空间
     *
     * @return 命名空间
     */
    public String getNamespace() {
        return ((CuratorFramework)jobRegistryCenter.getRawClient()).getNamespace();
    }

    private void startTrigger() {
        if (jobExecutorListener == null) {
            jobExecutorListener = new JobExecutorListener();
        }
        namespaceNodeStorage.addDataListener(jobExecutorListener);
    }

    private boolean isMonitorTriggerHistory(){
        if (!namespaceNodeStorage.isNodeExisted(GlobalNodePath.HISTORY)) return true;
        return Boolean.valueOf(namespaceNodeStorage.getNodeData(GlobalNodePath.HISTORY));
    }

    class JobExecutorListener extends AbstractJobListener {

        @Override
        protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
            if ((TreeCacheEvent.Type.NODE_ADDED == event.getType() || TreeCacheEvent.Type.NODE_UPDATED == event.getType())
                    && jobTriggerHistoryService.isBegin(path)) {
                log.debug("Job begin, path=" + path);
                jobTriggerHistoryService.add(jobRegistryCenter, path);
            }

            if ((TreeCacheEvent.Type.NODE_ADDED == event.getType() || TreeCacheEvent.Type.NODE_UPDATED == event.getType())
                    && jobTriggerHistoryService.isComplete(path)) {
                log.debug("Job complete, path=" + path);
                jobTriggerHistoryService.update(jobRegistryCenter, path);
            }
        }
    }

    class MonitorOptionChangedListener extends AbstractJobListener {

        @Override
        protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
            if ((TreeCacheEvent.Type.NODE_ADDED == event.getType() || TreeCacheEvent.Type.NODE_UPDATED == event.getType())
                    && path.equals(GlobalNodePath.HISTORY)) {
                log.info("Global config changed, path=" + path);
                if (Boolean.valueOf(jobRegistryCenter.get(path))) {
                    start();
                } else {
                    close();
                }
            }
        }
    }
}
