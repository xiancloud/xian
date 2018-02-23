package info.xiancloud.plugin.mq;

import info.xiancloud.plugin.init.Destroyable;

/**
 * @author happyyangyuan
 * MQ客户端父接口，懒加载模式，单例模式
 * ；仅供内部使用
 */
public interface IMqClient extends /*Initable, 要求实现为懒加载，所以不应当实现initable接口*/ Destroyable {

    /**
     * 子类必须实现本接口实现发布订阅前懒加载与mq服务器的连接
     */
    void initIfNotInitialized() throws Exception;

}
