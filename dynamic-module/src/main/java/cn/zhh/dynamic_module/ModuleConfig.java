package cn.zhh.dynamic_module;

import com.google.common.collect.Sets;

import java.util.Set;

public abstract class ModuleConfig {

    public abstract String getDesc();

    public Set<String> getScanPackages() {
        return Sets.newHashSet(this.getClass().getPackage().getName());
    }

    public Set<String> getOverridePackages() {
        return Sets.newHashSet();
    }

}