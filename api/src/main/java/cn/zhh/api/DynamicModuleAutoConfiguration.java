package cn.zhh.api;

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