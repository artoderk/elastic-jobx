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

package com.dangdang.ddframe.job.internal.reg;

import com.dangdang.ddframe.job.internal.storage.NamespaceNodeStorage;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心工厂.
 *
 * @author zhangliang
 * @author xiong.j
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegistryCenterFactory extends Observable{
    
    private static ConcurrentHashMap<HashCode, CoordinatorRegistryCenter> registryCenterMap = new ConcurrentHashMap<HashCode, CoordinatorRegistryCenter>();

    private static RegistryCenterFactory instance = new RegistryCenterFactory();
    
    /**
     * 创建注册中心.
     *
     * @param connectString 注册中心连接字符串
     * @param namespace 注册中心命名空间
     * @param digest 注册中心凭证
     * @return 注册中心对象
     */
    public static CoordinatorRegistryCenter createCoordinatorRegistryCenter(final String connectString, final String namespace, final Optional<String> digest) {
        return instance.innerCreate(connectString, namespace, digest);
    }

    private CoordinatorRegistryCenter innerCreate(final String connectString, final String namespace, final Optional<String> digest){
        HashCode hashCode = getHashCode(connectString, namespace, digest);
        if (registryCenterMap.containsKey(hashCode)) {
            return registryCenterMap.get(hashCode);
        } else {
            CoordinatorRegistryCenter result = null;
            synchronized (RegistryCenterFactory.class){
                if (registryCenterMap.containsKey(hashCode)) {
                    return registryCenterMap.get(hashCode);
                } else {
                    ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(connectString, namespace);
                    if (digest.isPresent()) {
                        zkConfig.setDigest(digest.get());
                    }
                    result = new ZookeeperRegistryCenter(zkConfig);
                    result.init();
                    registryCenterMap.putIfAbsent(hashCode, result);
                }
            }

            // 监听
            setChanged();
            notifyObservers(result);

            return result;
        }
    }

    public static RegistryCenterFactory getInstance(){
        return instance;
    }

    /**
     * 关闭注册中心.
     *
     * @param connectString 注册中心连接字符串
     * @param namespace 注册中心命名空间
     * @param digest 注册中心凭证
     */
    public void destroy(final String connectString, final String namespace, final Optional<String> digest){
        HashCode hashCode = getHashCode(connectString, namespace, digest);
        if (registryCenterMap.containsKey(hashCode)) {
            registryCenterMap.get(hashCode).close();
        }
    }

    /**
     * 关闭所有注册中心.
     *
     */
    public void destroy(){
        for (Map.Entry<HashCode, CoordinatorRegistryCenter> entry : registryCenterMap.entrySet()) {
            entry.getValue().close();
        }
    }

    private HashCode getHashCode(final String connectString, final String namespace, final Optional<String> digest) {
        Hasher hasher =  Hashing.md5().newHasher().putString(connectString, Charsets.UTF_8).putString(namespace, Charsets.UTF_8);
        if (digest.isPresent()) {
            hasher.putString(digest.get(), Charsets.UTF_8);
        }
        HashCode hashCode = hasher.hash();
        return hashCode;
    }
}
