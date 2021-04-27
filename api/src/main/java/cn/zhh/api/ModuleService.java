package cn.zhh.api;

/**
 * 模块服务类
 */
public interface ModuleService {

    /**
     * 加载并注册模块,会移除和卸载旧的模块
     *
     * @param moduleConfig 模块配置信息
     * @return 注册成功的模块, 如果模块不可用, 则返回null
     */
    Module loadAndRegister(ModuleConfig moduleConfig);

}