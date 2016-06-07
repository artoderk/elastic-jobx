package com.dangdang.ddframe.job.internal.sharding.strategy;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.dangdang.ddframe.job.exception.JobShardingStrategyClassConfigurationException;
import com.dangdang.ddframe.job.internal.sharding.strategy.fixture.InvalidJobShardingStrategy;
import com.dangdang.ddframe.job.plugin.sharding.strategy.AverageAllocationJobShardingStrategy;

public class JobShardingStrategyFactoryTest {
    
    @Test
    public void assertGetDefaultStrategy() {
        assertThat(JobShardingStrategyFactory.getStrategy(null), instanceOf(AverageAllocationJobShardingStrategy.class));
    }
    
//    @Test(expected = JobShardingStrategyClassConfigurationException.class)
//    public void assertGetStrategyFailureWhenClassNotFound() {
//        JobShardingStrategyFactory.getStrategy("NotClass");
//    }
    public void assertGetStrategyFailureWhenClassNotFound() {
    	// 出错取默认的分片策略
		assertThat(JobShardingStrategyFactory.getStrategy("NotClass"), instanceOf(AverageAllocationJobShardingStrategy.class));
	}
    
    @Test(expected = IllegalStateException.class)
    public void assertGetStrategyFailureWhenNotStrategyClass() {
        JobShardingStrategyFactory.getStrategy(Object.class.getName());
    }
    
    @Test(expected = JobShardingStrategyClassConfigurationException.class)
    public void assertGetStrategyFailureWhenStrategyClassInvalid() {
        JobShardingStrategyFactory.getStrategy(InvalidJobShardingStrategy.class.getName());
    }
    
    @Test
    public void assertGetStrategySuccess() {
        assertThat(JobShardingStrategyFactory.getStrategy(AverageAllocationJobShardingStrategy.class.getName()), instanceOf(AverageAllocationJobShardingStrategy.class));
    }
}
