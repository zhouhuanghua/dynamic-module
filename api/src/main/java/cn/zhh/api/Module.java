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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.context.ConfigurableApplicationContext;

import java.beans.Introspector;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkNotNull;

@Data
@Slf4j
@AllArgsConstructor
public class Module {

    private String fileName;

    private ModuleConfig moduleConfig;

    private ModuleApplicationContext moduleApplicationContext;

    public void destroy() {
        if (log.isInfoEnabled()) {
            log.info("Close application context: {}", moduleApplicationContext);
        }
        // close spring context
        closeQuietly(moduleApplicationContext);
        // clean classloader
        clear(moduleApplicationContext.getClassLoader());
    }

    private void closeQuietly(ConfigurableApplicationContext applicationContext) {
        checkNotNull(applicationContext, "applicationContext is null");
        try {
            applicationContext.close();
        } catch (Exception e) {
            log.error("Failed to close application context", e);
        }
    }

    public void clear(ClassLoader classLoader) {
        checkNotNull(classLoader, "classLoader is null");
        // Introspector缓存BeanInfo类来获得更好的性能。卸载时刷新所有Introspector的内部缓存。
        Introspector.flushCaches();
        // 从已经使用给定类加载器加载的缓存中移除所有资源包
        ResourceBundle.clearCache(classLoader);
        // Clear the introspection cache for the given ClassLoader
        CachedIntrospectionResults.clearClassLoader(classLoader);
        LogFactory.release(classLoader);
    }

}