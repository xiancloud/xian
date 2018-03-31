package info.xiancloud.core.support.zk;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;

import java.util.function.Consumer;

/**
 * 分布式锁帮助类
 * <p>
 * 如果觉得此分布式锁入口工具类使用起来不太方便，可以使用相同功能的{@link Synchronizer 更简洁的方式}替代
 *
 * @author happyyangyuan
 * @deprecated distributed lock based on zookeeper is proved to be under bad performances. Use redis distributed lock instead.
 */
public class DistLocker {

    /**
     * 加锁操作，阻塞直到你拿到锁为止，或者超时
     *
     * @param lockId         业务锁的id,保证全局唯一即可
     * @param timeoutInMilli 超时时间，单位毫秒
     * @param consumer       如果获取锁成功则返回那个可解锁的对象，否则返回一个失败的unit response 对象
     */
    public static void lock(String lockId, long timeoutInMilli, Consumer<UnitResponse> consumer) {
        Xian.call("zookeeper", "lock", new JSONObject() {{
            put("lockId", lockId);
            put("timeoutInMilli", timeoutInMilli);
        }}, consumer);
    }

    /**
     * 解锁
     */
    public static void unlock(Integer innerId, Consumer<UnitResponse> consumer) {
        Xian.call("zookeeper", "unlock", new JSONObject() {{
            put("innerId", innerId);
        }}, consumer);
    }

}
