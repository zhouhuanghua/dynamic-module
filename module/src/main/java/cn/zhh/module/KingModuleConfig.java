package cn.zhh.module;

import cn.zhh.api.MyModuleConfig;

public class KingModuleConfig implements MyModuleConfig {


    @Override
    public String getName() {
        return "King";
    }

    @Override
    public String getDesc() {
        return "Loops King";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
