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

package com.dangdang.ddframe.job.console.controller;

import com.dangdang.ddframe.job.console.domain.JobTriggerHistory;
import com.dangdang.ddframe.job.console.domain.RegistryCenterConfiguration;
import com.dangdang.ddframe.job.console.service.JobAPIService;
import com.dangdang.ddframe.job.console.service.JobTriggerHistoryService;
import com.dangdang.ddframe.job.console.util.SessionRegistryCenterConfiguration;
import com.dangdang.ddframe.job.domain.ExecutionInfo;
import com.dangdang.ddframe.job.domain.JobBriefInfo;
import com.dangdang.ddframe.job.domain.JobSettings;
import com.dangdang.ddframe.job.domain.ServerInfo;
import com.google.common.base.Strings;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Collection;

@RestController
@RequestMapping("job")
public class JobController {
    
    @Resource
    private JobAPIService jobAPIService;

    @Resource
    private JobTriggerHistoryService jobTriggerHistoryService;
    
    @RequestMapping(value = "jobs", method = RequestMethod.GET)
    public Collection<JobBriefInfo> getAllJobsBriefInfo() {
        return jobAPIService.getJobStatisticsAPI().getAllJobsBriefInfo();
    }
    
    @RequestMapping(value = "settings", method = RequestMethod.GET)
    public JobSettings getJobSettings(final JobSettings jobSettings, final ModelMap model) {
        model.put("jobName", jobSettings.getJobName());
        return jobAPIService.getJobSettingsAPI().getJobSettings(jobSettings.getJobName());
    }
    
    @RequestMapping(value = "settings", method = RequestMethod.POST)
    public void updateJobSettings(final JobSettings jobSettings) {
        jobAPIService.getJobSettingsAPI().updateJobSettings(jobSettings);
    }
    
    @RequestMapping(value = "servers", method = RequestMethod.GET)
    public Collection<ServerInfo> getServers(final ServerInfo jobServer) {
        return jobAPIService.getJobStatisticsAPI().getServers(jobServer.getJobName());
    }
    
    @RequestMapping(value = "execution", method = RequestMethod.GET)
    public Collection<ExecutionInfo> getExecutionInfo(final JobSettings config) {
        return jobAPIService.getJobStatisticsAPI().getExecutionInfo(config.getJobName());
    }

    @RequestMapping(value = "history", method = RequestMethod.GET)
    public Collection<JobTriggerHistory> getExecutionHistory(final JobSettings config) {
        JobTriggerHistory jobTriggerHistory = new JobTriggerHistory();
        RegistryCenterConfiguration regCenterConfig = SessionRegistryCenterConfiguration.getRegistryCenterConfiguration();
        jobTriggerHistory.setJobName(config.getJobName());
        jobTriggerHistory.setNamespace(regCenterConfig.getNamespace());
        if (Strings.isNullOrEmpty(jobTriggerHistory.getJobName()) || Strings.isNullOrEmpty(jobTriggerHistory.getNamespace())) {
            return null;
        }
        return jobTriggerHistoryService.list(jobTriggerHistory);
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public ModelAndView jobAddInit(final ModelMap model) {
        return new ModelAndView("job_add");
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public int jobAdd(final JobSettings jobSettings) {
        return jobAPIService.getJobSettingsAPI().addJobSettings(jobSettings);
    }
}
