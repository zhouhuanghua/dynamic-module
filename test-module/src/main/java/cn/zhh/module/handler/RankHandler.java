package cn.zhh.module.handler;

import cn.zhh.dynamic_module.Handler;
import org.springframework.stereotype.Component;

@Component
public class RankHandler implements Handler {

    public Object execute(String handlerArgs) {
        return handlerArgs;
    }

    public String getHandlerName() {
        return "rank";
    }

}
