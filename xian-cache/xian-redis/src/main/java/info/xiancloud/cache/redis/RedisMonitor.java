package info.xiancloud.cache.redis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.cache.redis.operate.ServerOperate;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class RedisMonitor {

    /**
     * 命中率
     *
     * @return
     */
    public static JSONArray monitorForKeyspaceHitRatio() {
        Map<String, Cache> CACHE = Redis.unmodifiableCache();

        JSONArray monitors = new JSONArray();

        if (CACHE == null || CACHE.isEmpty())
            return monitors;

        JSONObject monitor = new JSONObject();
        monitor.put("application", EnvUtil.getApplication());
        monitor.put("nodeId", LocalNodeManager.LOCAL_NODE_ID);

        for (Map.Entry<String, Cache> entry : CACHE.entrySet()) {
            Cache cache = entry.getValue();
            if (cache == null)
                continue;

            try (Jedis jedis = cache.getResource()) {
                monitor.put("instance", cache.getName());

                String info = ServerOperate.info(jedis, "stats");

                int keyspace_hits = Integer.parseInt(ServerOperate.getAttributeInInfo(info, "keyspace_hits")); // 查找数据库键成功的次数
                int keyspace_misses = Integer.parseInt(ServerOperate.getAttributeInInfo(info, "keyspace_misses")); // 查找数据库键失败的次数

                float keyspace_hit_ratio = 0F;
                try {
                    keyspace_hit_ratio = (keyspace_hits * 100.00F) / (keyspace_hits + keyspace_misses);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.error(e);
                }

                final float _keyspace_hit_ratio = keyspace_hit_ratio;

                monitors.add(new JSONObject() {{
                    putAll(monitor);

                    put("value", _keyspace_hit_ratio);
                    put("name", "KeyspaceHitRatio");
//                    put("attribute", "KeyspaceHitRatio");
                }});
            } catch (Exception e) {
                LOG.error(String.format("Jedis Pool: %s (Grafana) monitor 出现异常", entry.getKey()), e);
            }
        }

        return monitors;
    }

    /**
     * 内存信息
     *
     * @return
     */
    public static JSONArray monitorForMemory() {
        Map<String, Cache> CACHE = Redis.unmodifiableCache();

        JSONArray monitors = new JSONArray();

        if (CACHE == null || CACHE.isEmpty())
            return monitors;

        JSONObject monitor = new JSONObject();
        monitor.put("application", EnvUtil.getApplication());
        monitor.put("nodeId", LocalNodeManager.LOCAL_NODE_ID);

        for (Map.Entry<String, Cache> entry : CACHE.entrySet()) {
            Cache cache = entry.getValue();
            if (cache == null)
                continue;

            try (Jedis jedis = cache.getResource()) {
                monitor.put("instance", cache.getName());

                String info = ServerOperate.info(jedis, "memory");

                double used_memory = Double.parseDouble(ServerOperate.getAttributeInInfo(info, "used_memory")); // Redis 分配的内存总量
                monitors.add(new JSONObject() {{
                    putAll(monitor);

                    put("value", used_memory / 1024 / 1024); // N  / 1024 (KB) / 1024 (MB)
                    put("name", "UsedMemory");
                }});

//                double used_memory_peak = Double.parseDouble(ServerOperate.getAttributeInInfo(info, "used_memory_peak")); // Redis 的内存消耗峰值
//                monitors.add(new JSONObject() {{
//                    putAll(monitor);
//
//                    put("value", used_memory_peak / 1024 / 1024);
//                    put("name", "UsedMemoryPeak");
//                }});
            } catch (Exception e) {
                LOG.error(String.format("Jedis Pool: %s (Grafana) monitor 出现异常", entry.getKey()), e);
            }
        }

        return monitors;
    }

    /**
     * 健康检查
     *
     * @return
     */
    public static JSONArray monitorForHA() {
        Map<String, Cache> CACHE = Redis.unmodifiableCache();

        JSONArray monitors = new JSONArray();

        if (CACHE == null || CACHE.isEmpty())
            return monitors;

        JSONObject monitor = new JSONObject();
        monitor.put("application", EnvUtil.getApplication());
        monitor.put("nodeId", LocalNodeManager.LOCAL_NODE_ID);

        for (Map.Entry<String, Cache> entry : CACHE.entrySet()) {
            Cache cache = entry.getValue();
            if (cache == null)
                continue;

            try {
                monitor.put("instance", cache.getName());

                monitors.add(new JSONObject() {{
                    putAll(monitor);

                    put("value", cache.highAvailable() ? 1 : -1);
                    put("name", "HA");
                }});
            } catch (Exception e) {
                LOG.error(String.format("Jedis Pool: %s (Grafana) monitor 出现异常", entry.getKey()), e);
            }
        }

        return monitors;
    }

    /**
     * JedisPool 监控
     *
     * @return
     */
    public static JSONArray monitorForPool() {
        Map<String, Cache> CACHE = Redis.unmodifiableCache();

        JSONArray monitors = new JSONArray();

        if (CACHE == null || CACHE.isEmpty())
            return monitors;

        JSONObject monitor = new JSONObject();
        monitor.put("application", EnvUtil.getApplication());
        monitor.put("nodeId", LocalNodeManager.LOCAL_NODE_ID);

        for (Map.Entry<String, Cache> entry : CACHE.entrySet()) {
            try {
                Cache cache = entry.getValue();
                if (cache != null) {
                    monitor.put("instance", cache.getName());

                    monitors.add(new JSONObject() {{
                        putAll(monitor);

                        put("value", cache.getNumActive());
                        put("name", "numActive");
                    }});
//                    monitors.add(new JSONObject() {{
//                        putAll(monitor);
//
//                        put("value", cache.getNumIdle());
//                        put("name", "numIdle");
//                    }});
//                    monitors.add(new JSONObject() {{
//                        putAll(monitor);
//
//                        put("value", cache.getNumWaiters());
//                        put("name", "numWaiters");
//                    }});
                }
            } catch (Exception e) {
                LOG.error(String.format("Jedis Pool: %s (Grafana) monitor 出现异常", entry.getKey()), e);
            }
        }

        return monitors;
    }

}
