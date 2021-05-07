package cn.zhh.dynamic_module;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.nio.file.Paths;
import java.util.Objects;

public class DynamicModuleAutoConfigurationRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, @NonNull BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableDynamicModuleAutoConfiguration.class.getName()));
        if (Objects.isNull(annotationAttributes)) {
            throw new ModuleRuntimeException("@EnableDynamicModuleAutoConfiguration annotationAttributes is null");
        }
        String moduleJarAbsolutePath = annotationAttributes.getString("moduleJarAbsolutePath");
        // 创建目录
        Paths.get(moduleJarAbsolutePath).toFile().mkdirs();
        // 注册AddServlet
        registerBeanDefinition(beanDefinitionRegistry, moduleJarAbsolutePath, "newAddInstance", "addServlet");
        // 注册QueryServlet
        registerBeanDefinition(beanDefinitionRegistry, moduleJarAbsolutePath, "newQueryInstance", "queryServlet");
        // 注册RemoveServlet
        registerBeanDefinition(beanDefinitionRegistry, moduleJarAbsolutePath, "newRemoveInstance", "removeServlet");
    }

    private void registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, String moduleJarAbsolutePath, String factoryMethod, String beanName) {
        AbstractBeanDefinition removeBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ModuleManagerServletRegistrar.class)
                .setFactoryMethod(factoryMethod)
                .addPropertyValue("moduleJarAbsolutePath", moduleJarAbsolutePath)
                .addPropertyReference("moduleLoader", "moduleLoader")
                .addPropertyReference("moduleManager", "moduleManager")
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO)
                .getBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition(beanName, removeBeanDefinition);
    }

}