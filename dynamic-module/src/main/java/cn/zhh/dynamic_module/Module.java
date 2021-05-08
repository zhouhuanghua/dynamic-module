package cn.zhh.dynamic_module;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Data
@Slf4j
@AllArgsConstructor
class Module {

    private Path jarPath;

    private ModuleConfig moduleConfig;

    private ModuleApplicationContext moduleApplicationContext;
    
    private final Map<String, Handler> handlers;

    public Module(Path jarPath, ModuleConfig moduleConfig, ModuleApplicationContext moduleApplicationContext) {
        this.jarPath = jarPath;
        this.moduleConfig = moduleConfig;
        this.moduleApplicationContext = moduleApplicationContext;
        this.handlers = scanHandlers();
    }
    
    private Map<String, Handler> scanHandlers() {
        Map<String, Handler> handlers = Maps.newHashMap();
        // find Handler in module
        for (Handler handler : moduleApplicationContext.getBeansOfType(Handler.class).values()) {
            String handlerName = handler.name();
            if (!StringUtils.hasText(handlerName)) {
                throw new ModuleRuntimeException("scanHandlers handlerName is null");
            }
            checkState(!handlers.containsKey(handlerName), "Duplicated handler %s found by: %s",
                    Handler.class.getSimpleName(), handlerName);
            handlers.put(handlerName, handler);
        }
        if (log.isInfoEnabled()) {
            log.info("Scan handlers finish: {}", String.join(",", handlers.keySet()));
        }
        return ImmutableMap.copyOf(handlers);
    }

    public Handler getHandler(String handlerName) {
        checkNotNull(handlerName, "handlerName is null");
        Handler handler = handlers.get(handlerName);
        checkNotNull(handler, String.format("find handler is null, handlerName=%s", handlerName));
        return handler;
    }

    public Object doHandler(String handlerName, String handlerArgs) {
        checkNotNull(handlerName, "handlerName is null");
        checkNotNull(handlerArgs, "handlerArgs is null");
        return doHandlerWithinModuleClassLoader(getHandler(handlerName), handlerArgs);
    }

    private Object doHandlerWithinModuleClassLoader(Handler handler, String handlerArgs) {
        checkNotNull(handler, "handler is null");
        checkNotNull(handlerArgs, "handlerArgs is null");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader moduleClassLoader = handler.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(moduleClassLoader);
            return handler.execute(handlerArgs);
        } catch (Exception e) {
            log.error(String.format("Invoke module exception, handler=%s", handler.name()), e);
            throw new ModuleRuntimeException(String.format("doHandlerWithinModuleClassLoader has error, handler=%s", handler.getClass().getName()), e);
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    public void destroy() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Destroy module: name={}, version={}", moduleConfig.name(), moduleConfig.version());
        }
        // close spring context
        closeApplicationContext(moduleApplicationContext);
        // clean class loader
        clearClassLoader((URLClassLoader) moduleApplicationContext.getClassLoader());
        // delete jar file
        Files.deleteIfExists(jarPath);
    }

    private void closeApplicationContext(ConfigurableApplicationContext applicationContext) {
        checkNotNull(applicationContext, "applicationContext is null");
        try {
            applicationContext.close();
        } catch (Exception e) {
            log.error("Failed to close application context", e);
        }
    }

    private void clearClassLoader(URLClassLoader classLoader) throws IOException {
        checkNotNull(classLoader, "classLoader is null");
        // Introspector缓存BeanInfo类来获得更好的性能。卸载时刷新所有Introspector的内部缓存。
        Introspector.flushCaches();
        // 从已经使用给定类加载器加载的缓存中移除所有资源包
        ResourceBundle.clearCache(classLoader);
        // clear the introspection cache for the given ClassLoader
        CachedIntrospectionResults.clearClassLoader(classLoader);
        // close
        classLoader.close();
    }

}