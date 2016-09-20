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

import com.dangdang.ddframe.job.console.domain.RegistryCenterConfiguration;
import com.dangdang.ddframe.job.console.service.RegistryCenterService;
import com.dangdang.ddframe.job.internal.reg.RegistryCenterFactory;
import com.dangdang.ddframe.reg.exception.RegException;
import com.google.common.base.Optional;
import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("registry_center")
public class RegistryCenterController {
    
    public static final String REG_CENTER_CONFIG_KEY = "reg_center_config_key";
    
    @Resource
    private RegistryCenterService registryCenterService;
    
    @RequestMapping(method = RequestMethod.GET)
    public Collection<RegistryCenterConfiguration> load(final HttpSession session) {
        Optional<RegistryCenterConfiguration> regCenterConfig = registryCenterService.loadActivated();
        if (regCenterConfig.isPresent()) {
            setRegistryCenterNameToSession(regCenterConfig.get(), session);
        }
        return registryCenterService.loadAll();
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public int add(final RegistryCenterConfiguration config, final HttpSession session) {
        if (!validate(config)){
            return 2;  // 连接校验失败
        }
        if (registryCenterService.add(config)) {
            setRegistryCenterNameToSession(registryCenterService.load(config.getName()), session);
            return 0;  // 添加成功
        }
        return 1;  // 添加失败
    }
    
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public void delete(final RegistryCenterConfiguration config) {
        registryCenterService.delete(config.getName());
    }
    
    @RequestMapping(value = "connect", method = RequestMethod.POST)
    public boolean connect(final RegistryCenterConfiguration config, final HttpSession session) {
        return setRegistryCenterNameToSession(registryCenterService.load(config.getName()), session);
    }
    
    private boolean setRegistryCenterNameToSession(final RegistryCenterConfiguration regCenterConfig, final HttpSession session) {
        session.setAttribute(REG_CENTER_CONFIG_KEY, regCenterConfig);
        try {
            RegistryCenterFactory.createCoordinatorRegistryCenter(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), Optional.fromNullable(regCenterConfig.getDigest()));
        } catch (final RegException ex) {
            return false;
        }
        return true;
    }

    private boolean validate(final RegistryCenterConfiguration config) {

        String[] zkAddress = config.getZkAddressList().split(",");

        // 校验连接
        TelnetClient client = new TelnetClient();
        String hostname;
        int port;
        for (String address : zkAddress) {
            try {
                hostname = address.substring(0, address.indexOf(":")).trim();
                port = Integer.parseInt(address.substring(address.lastIndexOf(":") + 1).trim());

                client.setDefaultTimeout(5000);
                client.connect(hostname, port);
                if (client.isConnected()) {
                    return true;
                }
            } catch (Exception e1) {
            } finally {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    //
                }
            }
        }
        return false;
    }
}
