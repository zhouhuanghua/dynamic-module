package cn.zhh.dynamic_module;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.util.Map;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Data
@Slf4j
@AllArgsConstructor
public class Module {

    private String name;

    private ModuleConfig moduleConfig;

    private ModuleApplicationContext moduleApplicationContext;
    
    private final Map<String, Handler> handlers;

    public Module(String name, ModuleConfig moduleConfig, ModuleApplicationContext moduleApplicationContext) {
        this.name = name;
        this.moduleConfig = moduleConfig;
        this.moduleApplicationContext = moduleApplicationContext;
        this.handlers = scanHandlers();
    }
    
    private Map<String, Handler> scanHandlers() {
        Map<String, Handler> handlers = Maps.newHashMap();
        // find Handler in module
        for (Handler handler : moduleApplicationContext.getBeansOfType(Handler.class).values()) {
            String handlerName = handler.getHandlerName();
            if (!StringUtils.hasText(handlerName)) {
                throw new ModuleRuntimeException("scanHandlers handlerName is null");
            }
            checkState(!handlers.containsKey(handlerName), "Duplicated handler %s found by: %s",
                    Handler.class.getSimpleName(), handlerName);
            if (log.isInfoEnabled()) {
                log.info("Scan handler: {}", handlerName);
            }
            handlers.put(handlerName, handler);
        }
        if (log.isInfoEnabled()) {
            log.info("Scan handlers finish: {}", String.join(",", handlers.keySet()));
        }
        return ImmutableMap.copyOf(handlers);
    }

    public Map<String, Handler> getHandlers() {
        return handlers;
    }

    public Handler getHandler(String handlerName) {
        checkNotNull(handlerName, "handlerName is null");
        Handler handler = handlers.get(handlerName);
        checkNotNull(handler, "find handler is null, handlerName=" + handlerName);
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
            log.error("Invoke module exception, handler=" + handler.getHandlerName(), e);
            throw new ModuleRuntimeException("doHandlerWithinModuleClassLoader has error, handler=" + handler, e);
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    public void destroy() {
        if (log.isInfoEnabled()) {
            log.info("Close application context: {}", moduleApplicationContext);
        }
        // close spring context
        closeQuietly(moduleApplicationContext);
        // clean class loader
        clear(moduleApplicationContext.getClassLoader());
    }

    private void closeQuietly(ConfigurableApplicationContext applicationContext) {
        checkNotNull(applicationContext, "applicationContext is null");
        try {
            applicationContext.close();
        } catch (Exception e) {
            log.error("Failed to close application context", e);
        }
    }

    private void clear(ClassLoader classLoader) {
        checkNotNull(classLoader, "classLoader is null");
        // Introspector缓存BeanInfo类来获得更好的性能。卸载时刷新所有Introspector的内部缓存。
        Introspector.flushCaches();
        // 从已经使用给定类加载器加载的缓存中移除所有资源包
        ResourceBundle.clearCache(classLoader);
        // Clear the introspection cache for the given ClassLoader
        CachedIntrospectionResults.clearClassLoader(classLoader);
        LogFactory.release(classLoader);
    }

}