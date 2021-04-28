package cn.zhh.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class ModuleManager implements DisposableBean {
    
    /**
     * 已注册的所有模块
     */
    private final ConcurrentHashMap<String, Module> allModules = new ConcurrentHashMap<>();

    public Module register(Module module) {
        checkNotNull(module, "module is null");
        String name = module.getModuleConfig().getName();
        if (log.isInfoEnabled()) {
            log.info("Put Module: {}", module.getFileName());
        }

        return allModules.put(module.getFileName(), module);
    }

    public Module remove(String fileName) {
        checkNotNull(fileName, "file name is null");
        if (log.isInfoEnabled()) {
            log.info("Remove Module: {}", fileName);
        }
        return allModules.remove(fileName);
    }

    @Override
    public void destroy() {
        for (Module each : allModules.values()) {
            try {
                each.destroy();
            } catch (Exception e) {
                log.error("Failed to destroy module: " + each.getModuleConfig().getName(), e);
            }
        }
        allModules.clear();
    }

    public static String getModuleKey(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

}