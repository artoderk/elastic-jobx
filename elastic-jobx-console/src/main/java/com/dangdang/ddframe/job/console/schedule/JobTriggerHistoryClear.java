package com.dangdang.ddframe.job.console.schedule;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.console.domain.JobTriggerHistory;
import com.dangdang.ddframe.job.console.repository.JobTriggerHistoryRepository;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 作业执行状态数据清理Job
 *
 * Created by xiong.j on 2016/9/2.
 */
@Slf4j
@Component
public class JobTriggerHistoryClear extends AbstractSimpleElasticJob {

    private static final long DAY = 24 * 60 * 60 * 1000;

    private static final int DEFAULT_CLEAR_DAY = 30;

    @Autowired
    private JobTriggerHistoryRepository jobTriggerHistoryRepository;

    @Override
    public void process(final JobExecutionMultipleShardingContext context) {
        JobTriggerHistory jobTriggerHistory = new JobTriggerHistory();
        jobTriggerHistory.setBeginTime(getPrevDate(getShardingParam(context)));

        int count = jobTriggerHistoryRepository.del(jobTriggerHistory);
        log.info("Clear table job_trigger_history count=" + count);
    }

    private int getShardingParam(JobExecutionMultipleShardingContext context) {
        if (context.getShardingItemParameters() == null
                && (context.getShardingItemParameters().size() == 0 || context.getShardingItemParameters().size() > 0)) {
            return DEFAULT_CLEAR_DAY;
        } else {
            return Integer.parseInt(context.getShardingItemParameters().get(0));
        }
    }

    private Timestamp getPrevDate(int interval){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis() - DAY * interval);
        cal.set(Calendar.HOUR, -12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return new Timestamp(cal.getTimeInMillis());
    }
}