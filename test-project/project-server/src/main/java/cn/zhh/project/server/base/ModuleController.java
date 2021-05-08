package cn.zhh.project.server.base;

import cn.zhh.dynamic_module.ModuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("dynamic-module")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @RequestMapping(path = "{moduleName}/{moduleVersion}/{handlerName}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public Object invoke(@PathVariable String moduleName,
                         @PathVariable String moduleVersion,
                         @PathVariable String handlerName,
                         @RequestBody Map<String, Object> handlerArgs) {
        try {
            return moduleService.handle(moduleName, moduleVersion, handlerName, handlerArgs);
        } catch (Throwable t) {
            log.error(String.format("Invoke module handler exception: moduleName=%s, moduleVersion=%s, handlerName=%s, handlerArgs=%s",
                    moduleName, moduleVersion, handlerName, handlerArgs), t);
            throw new RuntimeException("invoke module handler exception", t);
        }
    }
}