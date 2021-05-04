package cn.zhh.module.handler;

import cn.zhh.dynamic_module.Handler;
import cn.zhh.dynamic_module.GsonUtils;
import cn.zhh.project.api.pojo.GetStudentByIdReq;
import cn.zhh.project.api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentHandler implements Handler {

    @Autowired
    private StudentService studentService;

    public Object execute(String handlerArgs) {
        GetStudentByIdReq req = GsonUtils.fromJson(handlerArgs, GetStudentByIdReq.class);
        return studentService.queryById(req.getId());
    }

    public String getHandlerName() {
        return "getStudentById";
    }

}
