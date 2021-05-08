package cn.zhh.dynamic_module;

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
class ModuleLoader implements ApplicationContextAware {

    /**
     * 注入父applicationContext
     */
    @Setter
    private ApplicationContext applicationContext;

    /**
     * 加载模块
     *
     * @param jarPath jar包路径
     * @return Module
     */
    public Module load(Path jarPath) {
        if (log.isInfoEnabled()) {
            log.info("Start to load module: {}", jarPath);
        }
        ModuleClassLoader moduleClassLoader;
        try {
            moduleClassLoader = new ModuleClassLoader(jarPath.toUri().toURL(), applicationContext.getClassLoader());
        } catch (MalformedURLException e) {
            throw new ModuleRuntimeException("create ModuleClassLoader exception", e);
        }
        List<ModuleConfig> moduleConfigList = new ArrayList<>();
        ServiceLoader.load(ModuleConfig.class, moduleClassLoader).forEach(moduleConfigList::add);
        if (moduleConfigList.size() != 1) {
            throw new ModuleRuntimeException("module config has and only has one");
        }
        ModuleConfig moduleConfig = moduleConfigList.get(0);
        moduleClassLoader.addOverridePackages(moduleConfig.overridePackages());
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // 把当前线程的ClassLoader切换成模块的
            Thread.currentThread().setContextClassLoader(moduleClassLoader);
            ModuleApplicationContext moduleApplicationContext = new ModuleApplicationContext();
            moduleApplicationContext.setParent(applicationContext);
            moduleApplicationContext.setClassLoader(moduleClassLoader);
            moduleApplicationContext.scan(moduleConfig.scanPackages().toArray(new String[0]));
            moduleApplicationContext.refresh();
            if (log.isInfoEnabled()) {
                log.info("Load module success: name={}, version={}, jarPath={}", moduleConfig.name(), moduleConfig.version(), jarPath);
            }
            return new Module(jarPath, moduleConfig, moduleApplicationContext);
        } catch (Throwable e) {
            log.error(String.format("Load module exception, jarPath=%s", jarPath), e);
            CachedIntrospectionResults.clearClassLoader(moduleClassLoader);
            throw new ModuleRuntimeException("create ModuleApplicationContext exception", e);
        } finally {
            // 还原当前线程的ClassLoader
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

}