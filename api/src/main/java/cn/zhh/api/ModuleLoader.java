package cn.zhh.api;

/**
 * 模块加载器
 */
public interface ModuleLoader {

    /**
     * 根据配置加载一个模块，创建一个新的ClassLoadr加载jar里的class，初始化Spring ApplicationContext等
     *
     * @param moduleConfig 模块配置信息
     * @return 加载成功的模块
     */
    Module load(ModuleConfig moduleConfig);

}
