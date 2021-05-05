package cn.zhh.dynamic_module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("modules")
@ConditionalOnBean(ModuleManager.class)
public class ModuleController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ModuleManager moduleManager;

    @RequestMapping(path = "{module}/{handler}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public Object invoke(@PathVariable String module, @PathVariable String handler, @RequestBody Map<String, Object> args) {
        try {
            return moduleManager.find(module).doHandler(handler, objectMapper.writeValueAsString(args));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
