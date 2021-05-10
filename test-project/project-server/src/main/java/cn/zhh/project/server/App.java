package cn.zhh.project.server;

import cn.zhh.dynamic_module.EnableDynamicModuleAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * APP
 */
@SpringBootApplication
@EnableDynamicModuleAutoConfiguration(moduleJarAbsolutePath = "/home/zhh/IdeaProjects/dynamic-module/test-project/module_jar")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}