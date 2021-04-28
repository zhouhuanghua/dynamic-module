/*
 *
 *  * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package cn.zhh.api;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@Slf4j
public class ModuleLoader implements ApplicationContextAware {

    /**
     * 注入父applicationContext
     */
    @Setter
    private ApplicationContext applicationContext;

    public Module load(Path jarPath) {

        ModuleClassLoader moduleClassLoader;
        try {
            moduleClassLoader = new ModuleClassLoader(jarPath.toUri().toURL(), applicationContext.getClassLoader());
        } catch (MalformedURLException e) {
            throw new ModuleRuntimeException("create classloader exception", e);
        }
        List<ModuleConfig> moduleConfigList = new ArrayList<>();
        ServiceLoader.load(ModuleConfig.class, moduleClassLoader).forEach(moduleConfigList::add);
        if (moduleConfigList.size() != 1) {
            throw new ModuleRuntimeException("module config has and only has one");
        }
        ModuleConfig moduleConfig = moduleConfigList.get(0);
        moduleClassLoader.addExcludedPackages(moduleConfig.getOverridePackages());

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // 把当前线程的ClassLoader切换成模块的
            Thread.currentThread().setContextClassLoader(moduleClassLoader);
            ModuleApplicationContext moduleApplicationContext = new ModuleApplicationContext();
            moduleApplicationContext.setParent(applicationContext);
            moduleApplicationContext.setClassLoader(moduleClassLoader);
            moduleApplicationContext.scan(moduleConfig.getScanPackages().toArray(new String[0]));
            moduleApplicationContext.refresh();
            log.info("Load module success: name={}, desc={}", moduleConfig.getName(), moduleConfig.getDesc());
            return new Module(jarPath.toString(), moduleConfig, moduleApplicationContext);
        } catch (Throwable e) {
            CachedIntrospectionResults.clearClassLoader(moduleClassLoader);
            throw new ModuleRuntimeException("init ModuleApplicationContext exception", e);
        } finally {
            // 还原当前线程的ClassLoader
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

}