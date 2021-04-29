package cn.zhh.module.handler;

import cn.zhh.api.Handler;
import org.springframework.stereotype.Component;

@Component
public class RankHandler implements Handler {

    @Override
    public Object execute(String handlerArgs) {
        return handlerArgs;
    }

    @Override
    public String getHandlerName() {
        return "rank";
    }
}
