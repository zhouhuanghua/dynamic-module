package cn.zhh.dynamic_module;

import lombok.Setter;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

public class ModuleManagerServletRegistrar extends ServletRegistrationBean<ModuleManagerServletRegistrar.ModuleManagerServlet> {
 
    @Setter
    private String jarDir;
 
    public ModuleManagerServletRegistrar() {
        super();
    }
 
    public static ModuleManagerServletRegistrar newInstance() {
        ModuleManagerServletRegistrar jobInvokeServletRegistrar = new ModuleManagerServletRegistrar();
        jobInvokeServletRegistrar.setServlet(jobInvokeServletRegistrar.new ModuleManagerServlet());
        jobInvokeServletRegistrar.setUrlMappings(Collections.singletonList("/module/add"));
        jobInvokeServletRegistrar.setLoadOnStartup(1);
 
        return jobInvokeServletRegistrar;
    }
 
    class ModuleManagerServlet extends HttpServlet {
 
        protected void service(HttpServletRequest req, HttpServletResponse resp) {

        }
    }
}