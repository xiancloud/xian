package info.xiancloud.core.support.zk;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;

import java.util.concurrent.TimeoutException;

/**
 * 分布式锁帮助类
 * <p>
 * 如果觉得此分布式锁入口工具类使用起来不太方便，可以使用相同功能的{@link Synchronizer 更简洁的方式}替代
 *
 * @author happyyangyuan
 * @deprecated distributed lock based on zookeeper is proved to be under bad performances. Use redis distributed lock instead.
 */
public class DistLocker {

    public static void main(String... args) {
        DistLocker locker = null;
        try {
            locker = DistLocker.lock("userWalletId-123456", 1000);
            LOG.info("各种同步代码");
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (locker != null)
                locker.unlock();
        }
    }

    /**
     * 加锁操作，阻塞直到你拿到锁为止，或者超时
     *
     * @param lockId         业务锁的id,保证全局唯一即可
     * @param timeoutInMilli 超时时间，单位毫秒
     * @return 如果获取锁成功则返回那个可解锁的对象，否则抛出超时异常
     */
    public static DistLocker lock(String lockId, long timeoutInMilli) throws TimeoutException {
        UnitResponse o = SyncXian.call("zookeeper", "lock", new JSONObject() {{
            put("lockId", lockId);
            put("timeoutInMilli", timeoutInMilli);
        }});
        if (o.succeeded()) {
            return new DistLocker(o.dataToInt());
        } else {
            throw new TimeoutException("获取锁超时:" + lockId);
        }
    }

    /**
     * 解锁
     */
    public void unlock() {
        SyncXian.call("zookeeper", "unlock", new JSONObject() {{
            put("innerId", innerId);
        }}).throwExceptionIfNotSuccess();
    }

    private DistLocker(Integer innerId) {
        this.innerId = innerId;
    }

    private Integer innerId;


}
