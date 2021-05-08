package cn.zhh.dynamic_module;

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

class ModuleManagerServletRegistrar extends ServletRegistrationBean<HttpServlet> {

    @Setter
    private String moduleJarAbsolutePath;
    @Setter
    private ModuleService moduleService;

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
                ModuleManagerServletRegistrar.this.moduleService.loadAndRegister(jarPath);
                resp.getWriter().write("OK");
            } catch (Throwable t) {
                t.printStackTrace(resp.getWriter());
            }
        }
    }

    private class ModuleQueryServlet extends HttpServlet {
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            try {
                String result = moduleService.queryAllModule();
                resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
                resp.getWriter().print(result);
            } catch (Throwable t) {
                t.printStackTrace(resp.getWriter());
            }
        }
    }

    private class ModuleRemoveServlet extends HttpServlet {
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            try {
                String moduleName = req.getParameter("moduleName"), moduleVersion = req.getParameter("moduleVersion");
                ModuleManagerServletRegistrar.this.moduleService.removeAndDestroy(moduleName, moduleVersion);
                resp.getWriter().write("OK");
            } catch (Throwable t) {
                t.printStackTrace(resp.getWriter());
            }
        }
    }

}