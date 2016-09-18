package com.dangdang.ddframe.job.console.zookeeper;

import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 管控平台注册中心
 *
 * Created by xiong.j on 2016/9/14.
 */
@Slf4j
@Component
public class ConsoleRegistryCenter {

    private CoordinatorRegistryCenter registryCenter;

    private LeaderLatch leaderLatch;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 连接Zookeeper服务器的列表.
     * 包括IP地址和端口号.
     * 多个地址用逗号分隔.
     * 如: host1:2181,host2:2181
     */
    @Value("${zk.serverLists:localhost:2181}")
    private String serverLists;

    /**
     * 命名空间.
     */
    @Value("${zk.namespace:elasticx-job-console}")
    private String namespace;

    /**
     * 等待重试的间隔时间的初始值.
     * 单位毫秒.
     */
    @Value("${zk.baseSleepTimeMilliseconds:1000}")
    private int baseSleepTimeMilliseconds;

    /**
     * 等待重试的间隔时间的最大值.
     * 单位毫秒.
     */
    @Value("${zk.maxSleepTimeMilliseconds:3000}")
    private int maxSleepTimeMilliseconds;

    /**
     * 最大重试次数.
     */
    @Value("${zk.maxRetries:3}")
    private int maxRetries = 3;

    /**
     * 会话超时时间.
     * 单位毫秒.
     */
    @Value("${zk.sessionTimeoutMilliseconds:0}")
    private int sessionTimeoutMilliseconds;

    /**
     * 连接超时时间.
     * 单位毫秒.
     */
    @Value("${zk.connectionTimeoutMilliseconds:0}")
    private int connectionTimeoutMilliseconds;

    /**
     * 连接Zookeeper的权限令牌.
     * 缺省为不需要权限验证.
     */
    @Value("${zk.digest:}")
    private String digest;

    /**
     * 连接ZK
     *
     * @return
     * @throws Exception
     */
    public ConsoleRegistryCenter init() throws Exception {
        try {
            // 连接ZK
            registryCenter = new ZookeeperRegistryCenter(getConfig());
            registryCenter.init();
            getClient().getConnectionStateListenable().addListener(new ConnectionLostListener());
            log.info("Connected to zookeeper :server=" + serverLists + ", namespace=" + namespace);
        } catch (Exception e) {
            log.error("Failed to connect zookeeper :server=" + serverLists + ", namespace=" + namespace, e);
            throw e;
        }
        return this;
    }

    /**
     * 添加连接状态监听器
     *
     * @param listener
     * @return
     */
    public ConsoleRegistryCenter addConnectionLostListener(final ConnectionLostListener listener) {
        if (listener != null) {
            getClient().getConnectionStateListenable().addListener(listener);
        }

        return this;
    }

    /**
     * 异步选主，只能启动一次
     *
     * @param listener
     */
    public void startLeaderElect(final LeaderLatchListener listener) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                boolean errFlag = true;
                leaderLatch = new LeaderLatch((CuratorFramework) registryCenter.getRawClient(), "/latch");
                leaderLatch.addListener(listener);
                do {
                    try {
                        leaderLatch.start();
                        leaderLatch.await();
                    } catch (Exception e) {
                        log.error("Failed to elect a Leader! will retry", e);
                        errFlag = false;
                    }
                } while (!errFlag);
            }
        });
    }

    /**
     * 是否Leader
     *
     * @return
     */
    public boolean hasLeadership() {
        return leaderLatch != null ? leaderLatch.hasLeadership() : false;
    }

    /**
     * 直接获取操作注册中心的原生客户端
     *
     * @return
     */
    public CuratorFramework getClient() {
        return (CuratorFramework) registryCenter.getRawClient();
    }

    /**
     * 关闭注册中心
     */
    public void close() {
        registryCenter.close();
        executor.shutdown();
    }

    private ZookeeperConfiguration getConfig(){
        ZookeeperConfiguration config = new ZookeeperConfiguration(serverLists, namespace, baseSleepTimeMilliseconds, maxSleepTimeMilliseconds, maxRetries);
        config.setSessionTimeoutMilliseconds(sessionTimeoutMilliseconds);
        config.setConnectionTimeoutMilliseconds(connectionTimeoutMilliseconds);
        config.setDigest(digest);
        return config;
    }

    class ConnectionLostListener implements ConnectionStateListener {

        @Override
        public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
            if (ConnectionState.LOST == newState) {
                log.info("Connection lost from zookeeper :server=" + serverLists + ", namespace=" + namespace);
            }

            if (ConnectionState.RECONNECTED == newState) {
                log.info("Reconnection from zookeeper :server=" + serverLists + ", namespace=" + namespace);
            }
        }
    }
}
