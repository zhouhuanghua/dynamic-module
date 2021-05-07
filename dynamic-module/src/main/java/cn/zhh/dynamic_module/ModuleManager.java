package cn.zhh.dynamic_module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.Collection;
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
        String key = toKey(module.getModuleConfig().name(), module.getModuleConfig().version());
        if (log.isInfoEnabled()) {
            log.info("Put module: {}", key);
        }
        return allModules.put(key, module);
    }

    public Collection<Module> allModules() {
        return allModules.values();
    }

    public Module find(String moduleName, String moduleVersion) {
        checkNotNull(moduleName, "module name is null");
        checkNotNull(moduleVersion, "module version is null");
        return allModules.get(toKey(moduleName, moduleVersion));
    }

    public Module remove(String moduleName, String moduleVersion) {
        checkNotNull(moduleName, "module name is null");
        checkNotNull(moduleVersion, "module version is null");
        String key = toKey(moduleName, moduleVersion);
        if (log.isInfoEnabled()) {
            log.info("Remove module: {}", key);
        }
        return allModules.remove(key);
    }

    @Override
    public void destroy() {
        for (Module each : allModules.values()) {
            try {
                each.destroy();
            } catch (Exception e) {
                log.error("Failed to destroy module: " + toKey(each.getModuleConfig().name(), each.getModuleConfig().version()), e);
            }
        }
        allModules.clear();
    }

    private String toKey(String moduleName, String moduleVersion) {
        return moduleName + "#" + moduleVersion;
    }

}