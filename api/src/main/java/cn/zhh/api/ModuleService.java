package cn.zhh.api;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.*;

@Slf4j
public class ModuleService {

    @Setter
    private String watchPath;

    @Autowired
    private ModuleManager moduleManager;
    @Autowired
    private ModuleLoader  moduleLoader;

    public void init() {
        // 注册所有Module
        loadExistModule();
        // 开启监听
        new JarWatchThread().start();
    }

    private void loadExistModule() {
        Paths.get(watchPath).forEach(this::loadAndRegister);
    }

    public void destroy() {
        moduleManager.destroy();
    }

    private class JarWatchThread extends Thread {
        private  JarWatchThread() {
            super("jar-watch-thread");
        }

        @Override
        public void run() {
            try {
                // 检测变化
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Paths.get(watchPath).register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                for (; ; ) {
                    // 得到一个key及其事件
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        // 检查是否为创建事件
                        if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                            log.info("【Dynamic-Module】 create file event: {}", event.context());
                            loadAndRegister(Paths.get(watchPath, event.context().toString()));
                        } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
                            log.info("【Dynamic-Module】 modify file event: {}", event.context());
                            loadAndRegister(Paths.get(watchPath, event.context().toString()));
                        } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
                            log.info("【Dynamic-Module】 delete file event: {}", event.context());
                            removeModule(event.context().toString());
                        } else {
                            log.info("【Dynamic-Module】 file watch invalid event: {}", event.context());
                        }
                    }
                    // 重置检测key
                    if (!key.reset()) {
                        throw new ModuleRuntimeException("Dynamic module file watch invalid key");
                    }
                }
            } catch (Throwable t) {
                throw new ModuleRuntimeException("Dynamic module run exception", t);
            }
        }
    }

    public Module loadAndRegister(Path jarPath) {
        Module module = moduleLoader.load(jarPath);
        Module oldModule = moduleManager.register(module);
        destroyQuietly(oldModule);
        return module;
    }

    private Module removeModule(String fileName) {
        Module removed = moduleManager.remove(fileName);
        destroyQuietly(removed);
        return removed;
    }

    private static void destroyQuietly(Module module) {
        if (module != null) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Destroy module: {}", module.getModuleConfig().getName());
                }
                module.destroy();
            } catch (Exception e) {
                log.error("Failed to destroy module " + module, e);
            }
        }
    }

}