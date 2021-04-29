package cn.zhh;

import cn.zhh.api.EnableDynamicModuleAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * APP
 */
@SpringBootApplication
@EnableDynamicModuleAutoConfiguration(moduleJarAbsolutePath = "/home/zhh/IdeaProjects/dynamic-module/project/module")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
