package com.dangdang.ddframe.job.internal.sharding.strategy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.dangdang.ddframe.job.internal.config.global.GlobalConfigurationService;

/**
 * 作业分片策略工厂.
 * 
 * @author zhangliang
 * @author xiong.j 重构 增加动态策略功能
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JobShardingStrategyFactory {
    /**
     * 获取 作业分片策略实例.
     * 
     * @param jobShardingStrategyClassName 分片策略类全路径
     * @return 分片策略类实例
     */
    public static JobShardingStrategy getStrategy(final String jobShardingStrategyClassName) {
    	return getStrategy(null, jobShardingStrategyClassName);
    	
    	/*if (Strings.isNullOrEmpty(jobShardingStrategyClassName)) {
            return new AverageAllocationJobShardingStrategy();
        }
        try {
            Class<?> jobShardingStrategyClass = Class.forName(jobShardingStrategyClassName);
            Preconditions.checkState(JobShardingStrategy.class.isAssignableFrom(jobShardingStrategyClass), String.format("Class [%s] is not job strategy class", jobShardingStrategyClassName));
            return (JobShardingStrategy) jobShardingStrategyClass.newInstance();
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new JobShardingStrategyClassConfigurationException(ex);
        }*/
    }

    /**
     * 获取 作业分片策略实例.
     * 
     * @param globalConfigService 全局配置服务
     * @param jobShardingStrategyFullPath 分片策略类全路径
     * @return 分片策略类实例
     */
    public static JobShardingStrategy getStrategy(final GlobalConfigurationService globalConfigService, final String jobShardingStrategyFullPath) {
    	return JobShardingStrategyCache.getStrategy(globalConfigService, jobShardingStrategyFullPath);
    }
}
