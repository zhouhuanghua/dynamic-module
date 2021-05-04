package cn.zhh.project.server;

import cn.zhh.dynamic_module.EnableDynamicModuleAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * APP
 */
@SpringBootApplication
@EnableDynamicModuleAutoConfiguration(moduleJarAbsolutePath = "C:\\Users\\z_hh\\Desktop\\dynamic-module-jar")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
