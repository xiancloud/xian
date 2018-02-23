package info.xiancloud.cache.service.unit.monitor;

import info.xiancloud.cache.redis.RedisMonitor;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.support.falcon.AbstractDiyMonitorUnit;
import info.xiancloud.plugin.util.EnvUtil;

public class JedisPoolMonitorUnit extends AbstractDiyMonitorUnit {
    @Override
    public String getName() {
        return "jedisPoolMonitor";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Redis 连接池状态监控")
                .setBroadcast(UnitMeta.Broadcast.create().setAsync(false).setSuccessDataOnly(true))
                .setPublic(false)
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
        return "Redis 连接池状态监控";
    }

    @Override
    public Object execute0() {
        return UnitResponse.success(RedisMonitor.monitorForPool());
    }

}
