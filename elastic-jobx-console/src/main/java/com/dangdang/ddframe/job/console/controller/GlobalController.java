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

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dangdang.ddframe.job.console.service.JobAPIService;
import com.dangdang.ddframe.job.domain.GlobalConfig;
import com.dangdang.ddframe.job.domain.GlobalStrategy;

/**
 * 全局配置控制器.
 * 
 * @author xiong.j
 */
@RestController
@RequestMapping("global")
public class GlobalController {
    
    @Resource
    private JobAPIService jobAPIService;
    
    @RequestMapping(value = "configs", method = RequestMethod.GET)
    public GlobalConfig getGlobalConfig() {
        return jobAPIService.getGlobalSettingsAPI().getGlobalConfigs();
    }

    @RequestMapping(value = "configs", method = RequestMethod.POST)
    public void updGlobalConfig(final GlobalConfig globalConfig) {
        jobAPIService.getGlobalSettingsAPI().updateGlobalConfigs(globalConfig);
    }
    
    @RequestMapping(value = "strategies", method = RequestMethod.GET)
    public Collection<GlobalStrategy> getStrategies() {
        return jobAPIService.getGlobalSettingsAPI().getGlobalStrategies();
    }

    @RequestMapping(value = "strategy", method = RequestMethod.POST)
    public int updStrategy(@RequestParam("strategyFile") MultipartFile file, 
    		@RequestParam("strategyName") String path) {
    	int result = 0; 
    	GlobalStrategy globalStrategy = new GlobalStrategy();
    	try {
			globalStrategy.setContent(new String(file.getBytes(), "UTF-8"));
			globalStrategy.setPath(path);
			jobAPIService.getGlobalSettingsAPI().updateGlobalStrategy(globalStrategy);
			result = 1;
		} catch (IOException e) {
			//
		}
        return result;
    }

    @RequestMapping(value = "delStrategy", method = RequestMethod.POST)
    public void delStrategy(final String name) {
        jobAPIService.getGlobalSettingsAPI().removeGlobalStrategy(name);
    }

}
