package cn.zhh.dynamic_module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

@Slf4j
class DynamicModuleAutoConfiguration {

    @PostConstruct
    public void init() {
        if (log.isInfoEnabled()) {
            log.info("Start to load DynamicModuleAutoConfiguration...");
        }
    }

    @Bean
    public ModuleLoader moduleLoader() {
        return new ModuleLoader();
    }

    @Bean
    public ModuleManager moduleManager() {
        return new ModuleManager();
    }

    @Bean
    public ModuleService moduleService() {
        return new ModuleService();
    }

}