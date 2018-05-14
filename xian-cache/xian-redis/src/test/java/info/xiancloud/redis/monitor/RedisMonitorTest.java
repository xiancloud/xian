package info.xiancloud.redis.monitor;


import com.alibaba.fastjson.JSONArray;
import info.xiancloud.cache.redis.RedisMonitor;
import info.xiancloud.redis.BaseRedis;

public class RedisMonitorTest extends BaseRedis {

    public static void main(String[] args) {

        keyspaceHitRatio();

    }

    protected static void keyspaceHitRatio() {
        JSONArray monitor = RedisMonitor.monitorForKeyspaceHitRatio();

        System.out.println(monitor.toJSONString());
    }

}