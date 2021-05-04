package cn.zhh.dynamic_module;

public interface Handler {

    Object execute(String handlerArgs);

    String getHandlerName();

}