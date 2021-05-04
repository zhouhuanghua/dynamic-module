package cn.zhh.dynamic_module;

import cn.zhh.dynamic_module.ModuleLoader;
import cn.zhh.dynamic_module.ModuleManager;
import org.springframework.context.annotation.Bean;

public class DynamicModuleAutoConfiguration {

    @Bean
    public ModuleLoader moduleLoader() {
        return new ModuleLoader();
    }

    @Bean
    public ModuleManager moduleManager() {
        return new ModuleManager();
    }
}