package cn.zhh.dynamic_module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class ModuleService {

    @Autowired
    private ModuleLoader moduleLoader;
    @Autowired
    private ModuleManager moduleManager;

    public void loadAndRegister(Path jarPath) {
        Module module = moduleLoader.load(jarPath);
        Module oldModule = moduleManager.register(module);
        destroyModule(oldModule);
    }

    public void removeAndDestroy(String moduleName, String moduleVersion) {
        Module module = moduleManager.remove(moduleName, moduleVersion);
        destroyModule(module);
    }

    private void destroyModule(Module module) {
        if (Objects.nonNull(module)) {
            try {
                module.destroy();
            } catch (Exception e) {
                log.error(String.format("Failed to destroy module: name=%s, version=%s", module.getModuleConfig().name(), module.getModuleConfig().version()), e);
            }
        }
    }

    public Object handle(String moduleName, String moduleVersion, String handlerName, Map<String, Object> handlerArgs) {
        Module module = moduleManager.find(moduleName, moduleVersion);
        if (Objects.isNull(module)) {
            throw new ModuleRuntimeException("module not exist");
        }
        return module.doHandler(handlerName, GsonUtils.toJson(handlerArgs));
    }

    public String queryAllModule() {
        List<ModuleVO> result = moduleManager.allModules().stream().map(module -> {
            ModuleConfig moduleConfig = module.getModuleConfig();
            return new ModuleVO(moduleConfig.name(), moduleConfig.version(), moduleConfig.desc(), module.getJarPath().toString());
        }).collect(Collectors.toList());
        return GsonUtils.toJson(result);
    }

    @Data
    @AllArgsConstructor
    private static class ModuleVO {
        private String name;
        private String version;
        private String desc;
        private String jarPath;
    }

}