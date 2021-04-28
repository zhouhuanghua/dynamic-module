package cn.zhh.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public interface MyModuleConfig {

    String getName();

    String getDesc();

    String getVersion();

    default String[] getScanPackages() {
        return new String[]{MyModuleConfig.class.getPackage().getName()};
    }

    default Map<String, Object> getProperties() {
        return Maps.newHashMap();
    }

    default List<String> getOverridePackages() {
        return Lists.newArrayList();
    }
}
