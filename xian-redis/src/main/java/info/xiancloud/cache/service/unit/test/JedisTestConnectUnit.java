package info.xiancloud.cache.service.unit.test;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.ObjectCacheOperate;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CountDownLatch;

/**
 * Redis Connect
 */
public class JedisTestConnectUnit implements Unit {
    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public String getName() {
        return "jedisTestConnect";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setPublic(false).setBroadcast();
    }

    @Override
    public Input getInput() {
        return new Input().add("number", int.class, "", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        int number = msg.get("number", int.class, 1000);
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        final CountDownLatch startCountDownLatch = new CountDownLatch(1);

        final CountDownLatch finishCountDownLatch = new CountDownLatch(number);

        for (int i = 0; i < number; i++) {
            ThreadPoolManager.execute(() -> {
                try {
                    startCountDownLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
                    for (int j = 0; j < 90; j++) {
                        ObjectCacheOperate.incr(jedis, "CONNECT");

                        Thread.sleep(1 * 1000);
                    }
                } catch (Exception e) {
                    LOG.error(e);
                }

                finishCountDownLatch.countDown();
            });
        }

        try {
            startCountDownLatch.countDown();

            finishCountDownLatch.await();

            try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
                String connect = ObjectCacheOperate.get(jedis, "CONNECT");

                LOG.info(String.format("CONNECT: %s", connect));
            } catch (Exception e) {
                LOG.error(e);
            }
        } catch (Exception e) {
            LOG.error(e);
        }

        SingleRxXian
                .call("diyMonitor", "jedisLockMonitor", new JSONObject())
                .subscribe(response -> handler.handle(UnitResponse.createSuccess()));
    }

}
