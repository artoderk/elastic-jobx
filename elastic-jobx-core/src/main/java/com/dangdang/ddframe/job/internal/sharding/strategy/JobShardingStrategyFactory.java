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
 * @author xiong.j
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class JobShardingStrategyFactory {

    /** 作业分片策略Class */
    public static final HashObjectPool<String, Class<?>> strategyClassPool = new HashObjectPool<String, Class<?>>();

    /**
     * 获取 作业分片策略实例.
     * 
     * @param jobShardingStrategyClassName 作业分片策略类名
     * @return 作业分片策略实例
     */
    public static JobShardingStrategy getStrategy(final String jobShardingStrategyClassName) {
        if (Strings.isNullOrEmpty(jobShardingStrategyClassName)) {
            return new AverageAllocationJobShardingStrategy();
        }
//        try {
        Class<?> jobShardingStrategyClass = getStrategyClass(jobShardingStrategyClassName);
        if (null == jobShardingStrategyClass) {
            throw new JobShardingStrategyClassConfigurationException(new Exception("ClassNotFoundException:" + jobShardingStrategyClassName));
        }
        JobShardingStrategy jobShardingStrategy = getInstance(jobShardingStrategyClass, jobShardingStrategyClassName);

        return jobShardingStrategy;
        
           // Preconditions.checkState(JobShardingStrategy.class.isAssignableFrom(jobShardingStrategyClass), String.format("Class [%s] is not job strategy class", jobShardingStrategyClassName));
           // return (JobShardingStrategy) jobShardingStrategyClass.newInstance();
//        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
//            throw new JobShardingStrategyClassConfigurationException(ex);
//        }
//	    } catch (final ClassNotFoundException ex1) {
//	        throw new JobShardingStrategyClassConfigurationException(ex1);
//	    } catch (final InstantiationException ex2) {
//	        throw new JobShardingStrategyClassConfigurationException(ex2);
//	    } catch (final IllegalAccessException ex3) {
//	        throw new JobShardingStrategyClassConfigurationException(ex3);
//	    }
    }

    /**
     * 获取 作业分片策略实例.
     * 
     * @param jobShardingStrategyClassName 作业分片策略类名
     * @return 作业分片策略实例
     */
    public static JobShardingStrategy getStrategy(final GlobalConfigurationService globalConfigService, final String jobShardingStrategyClassName) {
    	if (Strings.isNullOrEmpty(jobShardingStrategyClassName)) {
    		return new AverageAllocationJobShardingStrategy();
    	}
    	if (null == globalConfigService) {
    		return getStrategy(jobShardingStrategyClassName);
    	}
    	
    	JobShardingStrategy jobShardingStrategy = null;
    	
    	// 从本地获取作业分片策略
    	Class<?> clz = getStrategyClass(jobShardingStrategyClassName);
    	if (clz == null) {
    		String classType = jobShardingStrategyClassName;
    		String className = jobShardingStrategyClassName;
    		if (jobShardingStrategyClassName.indexOf(".") > 0) {
    			classType = jobShardingStrategyClassName.substring(0, jobShardingStrategyClassName.indexOf("."));
    			className = jobShardingStrategyClassName.substring(jobShardingStrategyClassName.lastIndexOf(".") + 1);
    		}
    		// 根据类型获取对应的编译器
    		DynamicCodeCompiler dynamicCodeCompiler = DynamicCodeCompilerFactory.getCompiler(classType);
    		try {
    			// 从注册中心获取全局分片策略类并编译
    			clz = dynamicCodeCompiler.compile(globalConfigService.getJobShardingStrategy(jobShardingStrategyClassName), className);
                jobShardingStrategy = getInstance(clz, jobShardingStrategyClassName);
			} catch (Exception e) {
                jobShardingStrategy = new AverageAllocationJobShardingStrategy();
				log.error("获取全局动态分片策略类出错，使用默认分片策略:[AverageAllocationJobShardingStrategy]", e);;
			}
    	}
    	
    	return jobShardingStrategy;
    }
    
    private static Class<?> getStrategyClass(final String jobShardingStrategyClassName) {
        Class<?> jobShardingStrategyClass = null;
		try {
			jobShardingStrategyClass = Class.forName(jobShardingStrategyClassName);
		} catch (ClassNotFoundException e) {
			//
		}

        if (jobShardingStrategyClass == null) {
            jobShardingStrategyClass = strategyClassPool.get(jobShardingStrategyClassName);
        }

        return jobShardingStrategyClass;
    }
    
    private static JobShardingStrategy getInstance(final Class<?> jobShardingStrategyClass, final String jobShardingStrategyClassName ) {
		try {
			Preconditions.checkState(JobShardingStrategy.class.isAssignableFrom(jobShardingStrategyClass),
					String.format("Class [%s] is not job strategy class", jobShardingStrategyClassName));
			return (JobShardingStrategy) jobShardingStrategyClass.newInstance();
		} catch (final InstantiationException ex2) {
			throw new JobShardingStrategyClassConfigurationException(ex2);
		} catch (final IllegalAccessException ex3) {
			throw new JobShardingStrategyClassConfigurationException(ex3);
		}
	}
}
