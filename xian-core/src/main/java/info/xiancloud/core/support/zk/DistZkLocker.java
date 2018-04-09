package info.xiancloud.core.support.zk;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitResponse;
import io.reactivex.Single;

import java.util.Objects;

/**
 * 分布式锁帮助类
 * <p>
 * 如果觉得此分布式锁入口工具类使用起来不太方便，可以使用相同功能的{@link ZkSynchronizer 更简洁的方式}替代
 *
 * @author happyyangyuan
 * @deprecated distributed lock based on zookeeper is proved to be under bad performances. Use redis distributed lock instead.
 */
public class DistZkLocker {

    /**
     * timed out waiting for the lock
     */
    public static final int TIME_OUT_INNER_ID = -1;

    /**
     * 加锁操作，阻塞直到你拿到锁为止，或者超时
     *
     * @param lockId         业务锁的id,保证全局唯一即可
     * @param timeoutInMilli 超时时间，单位毫秒，如果为0则表示如果不能马上获取锁，则不等待直接超时。
     * @return 如果获取锁成功则返回内部锁id，如果等待锁超时并返回{@link #TIME_OUT_INNER_ID -1}，如果其他失败原因则返回异常
     */
    public static Single<Integer> lock(String lockId, long timeoutInMilli) {
        return SingleRxXian.call("zookeeper", "lock", new JSONObject() {{
            put("lockId", lockId);
            put("timeoutInMilli", timeoutInMilli);
        }}).flatMap(unitResponse -> {
            if (unitResponse.succeeded())
                return Single.just(Objects.requireNonNull(unitResponse.dataToInteger()));
            else if (Group.CODE_TIME_OUT.equals(unitResponse.getCode()))
                return Single.just(TIME_OUT_INNER_ID);
            else
                return Single.error(new Exception(unitResponse.toVoJSONString()));
        });
    }

    /**
     * unlock
     *
     * @param innerId the inner int id of the lock, note that this inner id is not the business lock id.
     * @return true if  unlock successfully , otherwise false.
     */
    public static Single<Boolean> unlock(Integer innerId) {
        return SingleRxXian.call("zookeeper", "unlock", new JSONObject() {{
            put("innerId", innerId);
        }}).map(UnitResponse::succeeded);
    }
}
