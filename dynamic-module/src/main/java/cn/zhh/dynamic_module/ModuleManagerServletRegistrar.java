package cn.zhh.dynamic_module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManagerServletRegistrar extends ServletRegistrationBean<HttpServlet> {

    @Setter
    private String moduleJarAbsolutePath;

    @Setter
    private ModuleLoader moduleLoader;

    @Setter
    private ModuleManager moduleManager;

    public ModuleManagerServletRegistrar() {
        super();
    }

    public static ModuleManagerServletRegistrar newAddInstance() {
        ModuleManagerServletRegistrar addRegistrar = new ModuleManagerServletRegistrar();
        addRegistrar.setServlet(addRegistrar.new ModuleAddServlet());
        addRegistrar.setUrlMappings(Collections.singletonList("/module/add"));
        addRegistrar.setLoadOnStartup(1);
        addRegistrar.setMultipartConfig(new MultipartConfigElement("/tmp/upload"));
        return addRegistrar;
    }

    public static ModuleManagerServletRegistrar newQueryInstance() {
        ModuleManagerServletRegistrar queryRegistrar = new ModuleManagerServletRegistrar();
        queryRegistrar.setServlet(queryRegistrar.new ModuleQueryServlet());
        queryRegistrar.setUrlMappings(Collections.singletonList("/module/query"));
        queryRegistrar.setLoadOnStartup(1);
        return queryRegistrar;
    }

    public static ModuleManagerServletRegistrar newRemoveInstance() {
        ModuleManagerServletRegistrar removeRegistrar = new ModuleManagerServletRegistrar();
        removeRegistrar.setServlet(removeRegistrar.new ModuleRemoveServlet());
        removeRegistrar.setUrlMappings(Collections.singletonList("/module/remove"));
        removeRegistrar.setLoadOnStartup(1);
        return removeRegistrar;
    }

    private class ModuleAddServlet extends HttpServlet {

        private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

        protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            try {
                Path jarPath = Paths.get(ModuleManagerServletRegistrar.this.moduleJarAbsolutePath, DTF.format(LocalDateTime.now()) + ".jar");
                Files.copy(req.getPart("jar").getInputStream(), jarPath, StandardCopyOption.REPLACE_EXISTING);
                Module module = ModuleManagerServletRegistrar.this.moduleLoader.load(jarPath);
                ModuleManagerServletRegistrar.this.moduleManager.register(module);
                resp.getWriter().write("OK");
            } catch (Throwable t) {
                t.printStackTrace(resp.getWriter());
            }
        }
    }

    private class ModuleQueryServlet extends HttpServlet {

        protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            try {
                List<ModuleVO> result = ModuleManagerServletRegistrar.this.moduleManager.allModules().stream().map(module -> {
                    ModuleConfig moduleConfig = module.getModuleConfig();
                    return new ModuleVO(moduleConfig.name(), moduleConfig.version(), moduleConfig.desc(), module.getJarPath().toString());
                }).collect(Collectors.toList());
                resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
                resp.getWriter().print(GsonUtils.toJson(result));
            } catch (Throwable t) {
                t.printStackTrace(resp.getWriter());
            }
        }

        @Data
        @AllArgsConstructor
        private class ModuleVO {
            private String name;
            private String version;
            private String desc;
            private String jarPath;
        }
    }

    private class ModuleRemoveServlet extends HttpServlet {

        protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            try {
                String moduleName = req.getParameter("moduleName"), moduleVersion = req.getParameter("moduleVersion");
                Module module = ModuleManagerServletRegistrar.this.moduleManager.remove(moduleName, moduleVersion);
                module.destroy();
                Files.deleteIfExists(module.getJarPath());
                resp.getWriter().write("OK");
            } catch (Throwable t) {
                t.printStackTrace(resp.getWriter());
            }
        }
    }

}