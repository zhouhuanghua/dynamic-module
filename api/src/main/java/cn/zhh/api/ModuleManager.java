package cn.zhh.api;

import java.util.List;
import java.util.Map;

/**
 * module manager, 提供注册,移除和查找模块能力
 */
public interface ModuleManager {

    /**
     * find Module with name
     *
     * @param name module name
     * @return module instance
     */
    Module find(String name);

    /**
     * 根据模块名和版本查找Module
     *
     * @param name    模块名称
     * @param version 模块版本号
     * @return
     */
    Module find(String name, String version);

    /**
     * 激活模块的某个版本为默认版本
     *
     * @param name    module name
     * @param version module version
     * @return old module version
     */
    String activeVersion(String name, String version);

    /**
     * 获取所有已加载的Module
     *
     * @return
     */
    List<Module> getModules();

    /**
     * 注册一个Module
     *
     * @param module 模块
     * @return 旧模块, 如果没有旧模块则返回null
     */
    Module register(Module module);

    /**
     * 移除已激活版本的Module
     *
     * @param name 模块名
     * @return 被移除的模块
     */
    Module remove(String name);

    /**
     * 移除一个Module
     *
     * @param name    模块名
     * @param version 版本号
     * @return 被移除的模块
     */
    Module remove(String name, String version);

    /**
     * 获取发布失败的模块异常信息
     *
     * @return key:模块名,value:错误信息
     */
    Map<String, String> getErrorModuleContext();

}
