package cn.zhh.dynamic_module;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
class ScanJarPathRunner implements CommandLineRunner {

    @Setter
    private String moduleJarAbsolutePath;
    @Setter
    private boolean pathIsNewCreated;
    @Setter
    private ModuleService moduleService;

    @Override
    public void run(String... args) {
        if (pathIsNewCreated) {
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("Start to run scan jar path: {}", moduleJarAbsolutePath);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(moduleJarAbsolutePath), "*.jar")) {
            stream.forEach(moduleService::loadAndRegister);
        } catch (Exception e) {
            log.error("Load exist jar exception", e);
            throw new ModuleRuntimeException("load exist jar exception");
        }
    }

}