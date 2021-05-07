package cn.zhh.dynamic_module;

import lombok.Setter;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

public class ModuleManagerServletRegistrar extends ServletRegistrationBean<HttpServlet> {
 
    @Setter
    private String moduleJarAbsolutePath;

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
 
        protected void service(HttpServletRequest req, HttpServletResponse resp) {

        }
    }

    private class ModuleQueryServlet extends HttpServlet {

        protected void service(HttpServletRequest req, HttpServletResponse resp) {

        }
    }

    private class ModuleRemoveServlet extends HttpServlet {

        protected void service(HttpServletRequest req, HttpServletResponse resp) {

        }
    }

}