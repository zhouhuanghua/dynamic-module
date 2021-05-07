package cn.zhh.module;

import cn.zhh.dynamic_module.ModuleConfig;

public class KingModuleConfig extends ModuleConfig {

    @Override
    public String name() {
        return "king";
    }

    @Override
    public String version() {
        return "1.0";
    }

    public String desc() {
        return "Loops King";
    }

}