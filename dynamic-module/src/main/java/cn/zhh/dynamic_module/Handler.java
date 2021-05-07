package cn.zhh.dynamic_module;

public interface Handler {

    Object execute(String params);

    String name();

}