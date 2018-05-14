package info.xiancloud.cache.redis;

import redis.clients.jedis.Jedis;

public interface ICallback<T> {

    T call(Jedis jedis);

}
