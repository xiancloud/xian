package info.xiancloud.cache.service.unit.monitor;

import info.xiancloud.cache.redis.RedisMonitor;
import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.falcon.AbstractDiyMonitorUnit;
import info.xiancloud.core.util.EnvUtil;
import io.reactivex.Single;

/**
 * @author John_zero, happyyangyuan
 */
public class JedisHAMonitorUnit extends AbstractDiyMonitorUnit {
    @Override
    public String getName() {
        return "jedisHAMonitor";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Redis HA 监控")
                .setBroadcast(UnitMeta.Broadcast.create().setAsync(false).setSuccessDataOnly(true))
                .setDocApi(false)
                ;
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public String dashboard() {
        return EnvUtil.getShortEnvName() + "-redis";
    }

    @Override
    public String title() {
        return "Redis HA 监控";
    }

    @Override
    public Single<Object> execute0() {
        return Single.just(UnitResponse.createSuccess(RedisMonitor.monitorForHA()));
    }

}
