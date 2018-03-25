package info.xiancloud.core.event;

/**
 * 改造为泛型模式
 *
 * @author happyyangyuan
 */
public interface IEventListener {

    /**
     * 执行事件处理逻辑
     *
     * @param eventObject 监听到的事件对象
     */
    void onEvent(Object eventObject);

    /**
     * @return 监听目标事件类型
     */
    Class<?> getEventClass();

    /**
     * 是否异步执行事件onEvent方法
     *
     * @return 默认同步执行
     */
    default boolean async() {
        return false;
    }

}
