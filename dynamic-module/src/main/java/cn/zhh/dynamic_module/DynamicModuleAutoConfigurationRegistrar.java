package cn.zhh.dynamic_module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
class DynamicModuleAutoConfigurationRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata annotationMetadata, @NonNull BeanDefinitionRegistry beanDefinitionRegistry) {
        if (log.isInfoEnabled()) {
            log.info("Start to load DynamicModuleAutoConfigurationRegistrar...");
        }
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableDynamicModuleAutoConfiguration.class.getName()));
        if (Objects.isNull(annotationAttributes)) {
            throw new ModuleRuntimeException("@EnableDynamicModuleAutoConfiguration annotationAttributes is null");
        }
        String moduleJarAbsolutePath = annotationAttributes.getString("moduleJarAbsolutePath");

        // 创建目录
        boolean isNew = Paths.get(moduleJarAbsolutePath).toFile().mkdirs();
        // 注册AddServlet
        registerBeanDefinition(beanDefinitionRegistry, moduleJarAbsolutePath, "newAddInstance", "addServlet");
        // 注册QueryServlet
        registerBeanDefinition(beanDefinitionRegistry, moduleJarAbsolutePath, "newQueryInstance", "queryServlet");
        // 注册RemoveServlet
        registerBeanDefinition(beanDefinitionRegistry, moduleJarAbsolutePath, "newRemoveInstance", "removeServlet");

        // 注册启动扫码Runner
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ScanJarPathRunner.class)
                .addPropertyValue("moduleJarAbsolutePath", moduleJarAbsolutePath)
                .addPropertyValue("pathIsNewCreated", isNew)
                .addPropertyReference("moduleService", "moduleService")
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO)
                .getBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition("ScanJarPathRunner", beanDefinition);
    }

    private void registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, String moduleJarAbsolutePath, String factoryMethod, String beanName) {
        AbstractBeanDefinition removeBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ModuleManagerServletRegistrar.class)
                .setFactoryMethod(factoryMethod)
                .addPropertyValue("moduleJarAbsolutePath", moduleJarAbsolutePath)
                .addPropertyReference("moduleService", "moduleService")
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO)
                .getBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition(beanName, removeBeanDefinition);
    }

}