/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.job.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 作业运行时信息对象.
 *
 * @author zhangliang
 */
@Getter
@Setter
public final class ExecutionInfo implements Serializable, Comparable<ExecutionInfo> {
    
    private static final long serialVersionUID = 8587397581949456718L;
    
    private int item;
    
    private ExecutionStatus status;
    
    private String failoverIp;
    
    private Date lastBeginTime;
    
    private Date nextFireTime;
    
    private Date lastCompleteTime;
    
    @Override
    public int compareTo(final ExecutionInfo o) {
        return getItem() - o.getItem();
    }
    
    /**
     * 作业运行时状态.
     *
     * @author zhangliang
     */
    public enum ExecutionStatus {
        
        RUNNING(0, "RUNNING"),
        COMPLETED(1, "COMPLETED"),
        PENDING(2, "PENDING"),
        FAILED(9, "FAILED");

        private int    code;
        private String memo;

        ExecutionStatus(int code, String memo) {
            this.code = code;
            this.memo = memo;
        }

        /**
         * 获取作业运行时状态.
         * 
         * @param isRunning 是否在运行
         * @param isCompleted 是否运行完毕
         * @return 作业运行时状态
         */
        public static ExecutionStatus getExecutionStatus(final boolean isRunning, final boolean isCompleted) {
            if (isRunning) {
                return RUNNING;
            }
            if (isCompleted) {
                return COMPLETED;
            }
            return PENDING;
        }

        /**
         * 获取作业运行时状态.
         *
         * @param isRunning 是否在运行
         * @param isCompleted 是否运行完毕
         * @param isFailed 是否运行失败
         * @return 作业运行时状态
         */
        public static ExecutionStatus getExecutionStatus(final boolean isRunning, final boolean isCompleted, final boolean isFailed) {
            if (isRunning) {
                return RUNNING;
            }
            if (isCompleted) {
                return COMPLETED;
            }
            if (isFailed) {
                return FAILED;
            }
            return PENDING;
        }

        public static ExecutionStatus getEnum(int code) {
            for (ExecutionStatus item : values()) {
                //不区分大小写
                if (code == item.getCode()) {
                    return item;
                }
            }
            return null;
        }

        public int getCode() {
            return code;
        }

        public String getMemo() {
            return memo;
        }
    }
}
