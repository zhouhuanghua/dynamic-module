package cn.zhh.api;

/**
 * 模块的执行者
 */
public interface Action<R, T> {

    /**
     * 处理请求
     *
     * @param actionRequest 请求对象
     * @return 响应对象
     */
    T execute(R actionRequest);

    /**
     * 获取Action名称
     *
     * @return Action名称, 忽略大小写
     */
    String getActionName();

}