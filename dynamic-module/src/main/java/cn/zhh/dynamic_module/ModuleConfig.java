package cn.zhh.dynamic_module;

import com.google.common.collect.Sets;

import java.util.Set;

public abstract class ModuleConfig {

    public abstract String name();

    public abstract String version();

    public abstract String desc();

    public Set<String> scanPackages() {
        return Sets.newHashSet(this.getClass().getPackage().getName());
    }

    public Set<String> overridePackages() {
        return Sets.newHashSet();
    }

}