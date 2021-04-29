package cn.zhh.api;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

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
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(watchPath), "*.jar")) {
            stream.forEach(this::loadAndRegister);
        } catch (IOException e) {
            throw new ModuleRuntimeException("load exist jar exception");
        }
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
                        String fileName = event.context().toString();
                        // 只处理jar文件
                        if (!fileName.endsWith("jar")) {
                            break;
                        }
                        // 检查事件
                        if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                            if (log.isInfoEnabled()) {
                                log.info("【Dynamic-Module】 create file event: {}", fileName);
                            }
                            loadAndRegister(Paths.get(watchPath, fileName));
                        } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
                            if (log.isInfoEnabled()) {
                                log.info("【Dynamic-Module】 modify file event: {}", fileName);
                            }
                            loadAndRegister(Paths.get(watchPath, fileName));
                        } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
                            if (log.isInfoEnabled()) {
                                log.info("【Dynamic-Module】 delete file event: {}", fileName);
                            }
                            removeModule(fileName);
                        } else {
                            if (log.isInfoEnabled()) {
                                log.info("【Dynamic-Module】 file watch invalid event: {}", fileName);
                            }
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
        Module removed = moduleManager.remove(fileNameToModuleName(fileName));
        destroyQuietly(removed);
        return removed;
    }

    private String fileNameToModuleName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private void destroyQuietly(Module module) {
        if (Objects.nonNull(module)) {
            try {
                if (log.isInfoEnabled()) {
                    log.info("Destroy module: {}", module.getName());
                }
                module.destroy();
            } catch (Exception e) {
                log.error("Failed to destroy module: " + module.getName(), e);
            }
        }
    }

}