package com.dangdang.ddframe.job.internal.sharding.strategy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.dangdang.ddframe.job.exception.JobShardingStrategyClassConfigurationException;
import com.dangdang.ddframe.job.internal.config.GlobalConfigurationService;
import com.dangdang.ddframe.job.plugin.sharding.strategy.AverageAllocationJobShardingStrategy;
import com.dangdang.ddframe.util.compailer.DynamicCodeCompiler;
import com.dangdang.ddframe.util.compailer.DynamicCodeCompilerFactory;
import com.dangdang.ddframe.util.objectpool.HashObjectPool;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * 作业分片策略工厂.
 * 
 * @author zhangliang
 * @author xiong.j 重构 增加动态策略功能
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class JobShardingStrategyFactory {

    /** 作业分片策略Class */
    public static final HashObjectPool<String, JobShardingStrategy> strategyInstancePool = new HashObjectPool<String, JobShardingStrategy>();
    
    /** 默认作业分片类全路径 */
    public static final String DEFAULT_STRATEGY = "com.dangdang.ddframe.job.plugin.sharding.strategy.AverageAllocationJobShardingStrategy";

    static {
    	// 默认的分片策略类
    	strategyInstancePool.put(DEFAULT_STRATEGY, new AverageAllocationJobShardingStrategy());
    }
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
    	if (Strings.isNullOrEmpty(jobShardingStrategyFullPath)) {
    		return strategyInstancePool.get(DEFAULT_STRATEGY);
    	}
    	if (strategyInstancePool.containsKey(jobShardingStrategyFullPath)) {
    		return strategyInstancePool.get(jobShardingStrategyFullPath);
    	}
    	return getInnerStrategy(globalConfigService, jobShardingStrategyFullPath);
    }
    
    /**
     * 获取 作业分片策略实例主逻辑.
     * 
     * @param globalConfigService 全局配置服务
     * @param jobShardingStrategyFullPath 分片策略类全路径
     * @return 分片策略类实例
     */
    private static synchronized JobShardingStrategy getInnerStrategy(final GlobalConfigurationService globalConfigService, final String jobShardingStrategyFullPath) {
    	if (strategyInstancePool.containsKey(jobShardingStrategyFullPath)) {
    		return strategyInstancePool.get(jobShardingStrategyFullPath);
    	}

    	// 从本地获取作业分片策略
    	Class<?> clz = null;
		try {
			clz = getStrategyClass(globalConfigService, jobShardingStrategyFullPath);
		} catch (Throwable e) {
			log.error("获取全局动态分片策略类出错，使用默认分片策略:[AverageAllocationJobShardingStrategy]", e);
		}
    	return getInstance(clz, jobShardingStrategyFullPath);
    }
    
    /**
     * 
     * @param globalConfigService 全局配置服务
     * @param jobShardingStrategyFullPath 分片策略类全路径
     * @return 全局分片策略类
     * @throws Throwable exception
     */
    private static Class<?> getStrategyClass(final GlobalConfigurationService globalConfigService, final String jobShardingStrategyFullPath) throws Throwable {
        Class<?> jobShardingStrategyClass = null;
		try {
			jobShardingStrategyClass = Class.forName(jobShardingStrategyFullPath);
		} catch (ClassNotFoundException e) {
			//
		}
		
		if (null == jobShardingStrategyClass && null != globalConfigService) {
			jobShardingStrategyClass = getRemoteStrategyClass(globalConfigService, jobShardingStrategyFullPath);
		}
        return jobShardingStrategyClass;
    }
    
    /**
     * 从注册中心获取全局分片策略类并编译
     * 
     * @param globalConfigService 全局配置服务
     * @param jobShardingStrategyFullPath 分片策略类全路径
     * @return 全局分片策略类
     * @throws Throwable exception
     */
    private static Class<?> getRemoteStrategyClass(final GlobalConfigurationService globalConfigService, final String jobShardingStrategyFullPath) throws Throwable{
    	String classType = jobShardingStrategyFullPath;
		String className = jobShardingStrategyFullPath;
		if (jobShardingStrategyFullPath.indexOf(".") > 0) {
			classType = jobShardingStrategyFullPath.substring(0, jobShardingStrategyFullPath.indexOf("."));
			className = jobShardingStrategyFullPath.substring(jobShardingStrategyFullPath.lastIndexOf(".") + 1);
		}
		// 根据类型获取对应的编译器
		DynamicCodeCompiler dynamicCodeCompiler = DynamicCodeCompilerFactory.getCompiler(classType);
		// 从注册中心获取全局分片策略类并编译
		return dynamicCodeCompiler.compile(globalConfigService.getJobShardingStrategy(jobShardingStrategyFullPath), className);
    }
    
    /**
     * 获取分片策略类实例
     * 
     * @param jobShardingStrategyClass 类
     * @param jobShardingStrategyClassName 类名
     * @return 分片策略类实例
     */
    private static JobShardingStrategy getInstance(final Class<?> jobShardingStrategyClass, final String jobShardingStrategyClassName ) {
    	if (null == jobShardingStrategyClass) return strategyInstancePool.get(DEFAULT_STRATEGY);
    	
    	JobShardingStrategy strategy = null;
		try {
			Preconditions.checkState(JobShardingStrategy.class.isAssignableFrom(jobShardingStrategyClass),
					String.format("Class [%s] is not job strategy class", jobShardingStrategyClassName));
			strategy = (JobShardingStrategy) jobShardingStrategyClass.newInstance();
			strategyInstancePool.put(jobShardingStrategyClassName, strategy);
			return strategy;
		} catch (final InstantiationException ex2) {
			throw new JobShardingStrategyClassConfigurationException(ex2);
		} catch (final IllegalAccessException ex3) {
			throw new JobShardingStrategyClassConfigurationException(ex3);
		}
	}
}
