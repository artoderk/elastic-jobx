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

/**
 * 全局配置对象.
 * 
 * @author xiong.j
 */
@Getter
@Setter
public final class GlobalConfig implements Serializable {
    
	private static final long serialVersionUID = -6565345856912689299L;

	private String skipTimeStart;
    
    private String skipTimeEnd;

}
