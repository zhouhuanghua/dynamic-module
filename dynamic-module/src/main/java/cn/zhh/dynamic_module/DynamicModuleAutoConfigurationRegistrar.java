package cn.zhh.dynamic_module;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.util.Objects;

public class DynamicModuleAutoConfigurationRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, @NonNull BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableDynamicModuleAutoConfiguration.class.getName()));
        if (Objects.isNull(annotationAttributes)) {
            throw new ModuleRuntimeException("@EnableDynamicModuleAutoConfiguration annotationAttributes is null");
        }
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ModuleService.class)
                .setInitMethodName("init")
                .setDestroyMethodName("destroy")
                .addPropertyValue("watchPath", annotationAttributes.getString("moduleJarAbsolutePath"))
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                .getBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition("moduleService", beanDefinition);
    }

}