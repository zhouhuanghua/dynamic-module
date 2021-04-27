package cn.zhh.api.impl;

import cn.zhh.api.*;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模块服务默认实现
 */
public class ModuleServiceImpl implements ModuleService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ModuleServiceImpl.class);

    private ModuleManager moduleManager;
    private ModuleLoader moduleLoader;

    @Override
    public Module loadAndRegister(ModuleConfig moduleConfig) {
        Preconditions.checkNotNull(moduleConfig, "moduleConfig is null");
        //如果新模块可用,则加载并注册新模块,最后卸载旧模块
        if (moduleConfig.getEnabled()) {
            Module module = moduleLoader.load(moduleConfig);
            Module oldModule = moduleManager.register(module);
            destroyQuietly(oldModule);
            return module;
        }

        //新模块不可用则卸载旧模块
        removeModule(moduleConfig.getName());
        return null;
    }

    private Module removeModule(String moduleName) {
        Module removed = moduleManager.remove(moduleName);
        destroyQuietly(removed);
        return removed;
    }

    private static void destroyQuietly(Module module) {
        if (module != null) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Destroy module: {}", module.getName());
                }
                module.destroy();
            } catch (Exception e) {
                LOGGER.error("Failed to destroy module " + module, e);
            }
        }
    }

    public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public void setModuleLoader(ModuleLoader moduleLoader) {
        this.moduleLoader = moduleLoader;
    }

}