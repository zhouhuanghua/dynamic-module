package cn.zhh.api;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({DynamicModuleAutoConfigurationRegistrar.class, DynamicModuleAutoConfiguration.class})
public @interface EnableDynamicModuleAutoConfiguration {

    String moduleJarAbsolutePath();
}