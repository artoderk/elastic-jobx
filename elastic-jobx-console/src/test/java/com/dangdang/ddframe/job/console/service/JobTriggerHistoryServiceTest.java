package com.dangdang.ddframe.job.console.service;

import com.dangdang.ddframe.job.console.domain.JobTriggerHistory;
import com.dangdang.ddframe.job.console.repository.JobTriggerHistoryRepository;
import com.dangdang.ddframe.util.DateUtil;
import common.DefaultTestCase;
import lombok.extern.slf4j.Slf4j;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.sql.Timestamp;

/**
 * Created by xiong.j on 2016/9/20.
 */
@Slf4j
public class JobTriggerHistoryServiceTest extends DefaultTestCase{

    @InjectMocks
    @Autowired
    private JobTriggerHistoryRepository jobTriggerHistoryRepository;

    //@Test
    public void add() throws Exception {
        JobTriggerHistory jobTriggerHistory = new JobTriggerHistory();
        jobTriggerHistory.setNamespace("elasticjob-example");
        jobTriggerHistory.setJobName("SimpleJobDemo");
        jobTriggerHistory.setShardingItem(2);
        jobTriggerHistory.setBeginTime(new Timestamp(DateUtil.parseDate("2016-09-20 16:25:55.071", "yyyy-MM-dd hh:mm:ss.sss").getTime()));
        try {
            jobTriggerHistoryRepository.add(jobTriggerHistory);
        } catch (DuplicateKeyException e) {
            log.warn("Duplicate insert, ignoring it. cause:" + e.getCause().getMessage());
        }
    }

}