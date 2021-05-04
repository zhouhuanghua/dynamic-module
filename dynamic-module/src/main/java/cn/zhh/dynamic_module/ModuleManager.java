package cn.zhh.dynamic_module;

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
        String name = module.getName();
        if (log.isInfoEnabled()) {
            log.info("Put module: {}", name);
        }

        return allModules.put(name, module);
    }

    public Module find(String moduleName) {
        checkNotNull(moduleName, "module name is null");
        return allModules.get(moduleName);
    }

    public Module remove(String moduleName) {
        checkNotNull(moduleName, "module name is null");
        if (log.isInfoEnabled()) {
            log.info("Remove module: {}", moduleName);
        }
        return allModules.remove(moduleName);
    }

    @Override
    public void destroy() {
        for (Module each : allModules.values()) {
            try {
                each.destroy();
            } catch (Exception e) {
                log.error("Failed to destroy module: " + each.getName(), e);
            }
        }
        allModules.clear();
    }

}