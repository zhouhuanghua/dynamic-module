package cn.zhh.api;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public abstract class ModuleConfig {

    public abstract String getName();

    public abstract String getDesc();

    public abstract List<HttpAction> registerServlet();

    public Set<String> getScanPackages() {
        return Sets.newHashSet(ModuleConfig.class.getPackage().getName());
    }

    public Set<String> getOverridePackages() {
        return Sets.newHashSet();
    }

}