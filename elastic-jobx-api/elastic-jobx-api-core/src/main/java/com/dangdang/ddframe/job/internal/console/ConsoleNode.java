package com.dangdang.ddframe.job.internal.console;

/**
 * 后管平台特殊节点
 *
 * Created by xiong.j on 2016/10/12.
 */
public final class ConsoleNode {

    /**
     * 执行状态根节点.
     */
    public static final String ROOT = "console";

    /**
     * 后管平台Leader选举节点.
     */
    public static final String LATCH = "/" + ROOT + "/latch";

    /**
     * 后管平台待Leader监控的namespace节点.
     */
    public static final String NAMESPACE = "/" + ROOT + "/namespace";

    /**
     * 获取节点全路径.
     *
     * @param node 节点名称
     * @return 节点全路径
     */
    public static String getFullNamespacePath(final String node) {
        return String.format("%s/%s", NAMESPACE, node);
    }

}