package com.dangdang.ddframe.job.console.domain;

import com.dangdang.ddframe.job.domain.ExecutionInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * Created by xiong.j on 2016/9/2.
 */
@Getter
@Setter
@ToString
public class JobTriggerHistory {

    private long id = -1;

    private String namespace;

    private String jobName;

    private int shardingItem = -1;

    private int shardingCount = -1;

    private String failoverIp;

    private int status = -1;

    private String statusValue;

    private Timestamp beginTime;

    private Timestamp completeTime;

    private Timestamp nextFireTime;

    private long limit = -1;

    private int pageCount = -1;

    private int pageIndex = -1;
}
