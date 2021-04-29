package cn.zhh.api;

public interface Handler {

    Object execute(String handlerArgs);

    String getHandlerName();

}