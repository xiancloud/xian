package info.xiancloud.cache.redis;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Redis
 *
 * @author John_zero, happyyangyuan
 * @author happyyangyuan
 */
public final class Redis {

    private static CacheConfigBean CACHE_CONFIG_BEAN;

    static {
        try {
            CACHE_CONFIG_BEAN = new CacheConfigBean(getUrl(), getPassword(), getDBIndex());

            LOG.info("默认 Redis 数据源配置: " + CACHE_CONFIG_BEAN.toString());
        } catch (Throwable t) {
            LOG.error(t);
        }
    }

    private static String getUrl() {
        if (EnvUtil.isLan())
            return XianConfig.get("redisLanUrl"); // 腾讯云内网内
        else
            return XianConfig.get("redisInternetUrl"); // 外网
    }

    private static String getPassword() {
        return XianConfig.get("redisPassword");
    }

    protected static int getDBIndex() {
        return XianConfig.getIntValue("redisDbIndex");
    }

    private static final ConcurrentHashMap<String, Cache> CACHE = new ConcurrentHashMap<>();

    private static final Object LOCK = new Object();

    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> DOMAIN_NAME_TO_IP = new ConcurrentHashMap<>();

    // 默认端口
    public static final int PORT = 6379;
    // 默认DB下标
    public static final int DB_INDEX = 0;

    // 操作超时时间, 默认2秒
    public static final int TIME_OUT = 1800;

    private static final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

    static {
        // Jedis 池最大连接数总数, 默认8, 设置 -1 表示不限制
        jedisPoolConfig.setMaxTotal(9000);
        // Jedis 池最大空闲连接数, 默认8
        jedisPoolConfig.setMaxIdle(50);
        // Jedis 池最少空闲连接数
        jedisPoolConfig.setMinIdle(1);
        // Jedis 池没有对象返回时, 最大等待时间, 单位: 毫秒
        jedisPoolConfig.setMaxWaitMillis(3 * 500);
        // 空闲检查时间
        jedisPoolConfig.setMinEvictableIdleTimeMillis(10 * 1000);
        // 在 borrow(引入) 一个 Jedis 实例时, 是否提前进行 validate 操作
        jedisPoolConfig.setTestOnBorrow(true);
        // 在归还给 Jedis 池时, 是否提前进行 validate 操作
        jedisPoolConfig.setTestOnReturn(false);
        // 默认 30秒, 当前 5秒
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(5 * 1000);
    }

    private Redis() {

    }

    public static Map<String, Cache> unmodifiableCache() {
        return Collections.unmodifiableMap(CACHE);
    }

    @Deprecated
    public synchronized static void initRedis(String url, String password, int dbIndex) {
        CacheConfigBean cacheConfigBean = new CacheConfigBean(url, password, dbIndex);

        initRedis(cacheConfigBean.getHost(), cacheConfigBean.getPort(), cacheConfigBean.getPassword(), cacheConfigBean.getDbIndex(), jedisPoolConfig);
    }

    private synchronized static void initRedis(CacheConfigBean cacheConfigBean, JedisPoolConfig jedisPoolConfig) {
        initRedis(cacheConfigBean.getHost(), cacheConfigBean.getPort(), cacheConfigBean.getPassword(), cacheConfigBean.getDbIndex(), jedisPoolConfig);
    }

    public synchronized static void initRedis(String host, int port, String password, int dbIndex) {
        initRedis(host, port, password, dbIndex, jedisPoolConfig);
    }

    private synchronized static void initRedis(String host, int port, String password, int dbIndex, JedisPoolConfig jedisPoolConfig) {
        String name = dataSourceName(host);

        if (CACHE.containsKey(name))
            return;

        Cache cache = new Cache(name, host, port, TIME_OUT, password, dbIndex, jedisPoolConfig);

        Cache _cache = CACHE.putIfAbsent(cache.getName(), cache);

        if (_cache != null && _cache != cache) {
            if (!_cache.highAvailable()) {
                _cache.destroy();

                if (!isHostOrIP(host))
                    removeMapping(host, _cache.getName());

                CACHE.put(cache.getName(), cache);
            } else {
                cache.destroy();

                if (!isHostOrIP(host))
                    removeMapping(host, cache.getName());
            }
        }
    }

    /**
     * @param hostOrIP
     * @return true: IP, false: host
     */
    private static boolean isHostOrIP(String hostOrIP) {
        Pattern pattern = Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
        Matcher matcher = pattern.matcher(hostOrIP);
        return matcher.matches();
    }

    public static String dataSourceName(String host) {
        if (host == null)
            throw new NullPointerException("Host 或者 IP 为 Null");

        if (isHostOrIP(host)) // IP
        {
            return host;
        } else // 域名
        {
            synchronized (LOCK) {
                try {
                    // 暂时只考虑一个 HOST 映射一个 IP, 不考虑 DNS 映射多 IP 的情况
                    if (DOMAIN_NAME_TO_IP.containsKey(host) && !DOMAIN_NAME_TO_IP.get(host).isEmpty()) {
                        String ip = DOMAIN_NAME_TO_IP.get(host).get(0);

                        if (!EnvUtil.getEnv().equals(EnvUtil.PRODUCTION))
                            LOG.info(String.format("Redis, 缓存识别 Host: %s -> IP: %s, 累计缓存 IP 数量： %s", host, ip, DOMAIN_NAME_TO_IP.get(host).size()));

                        return ip;
                    }

                    InetAddress address = InetAddress.getByName(host);

                    String ip = address.getHostAddress();

                    if (DOMAIN_NAME_TO_IP.containsKey(host)) {
                        if (!DOMAIN_NAME_TO_IP.get(host).contains(ip))
                            DOMAIN_NAME_TO_IP.get(host).add(ip);
                    } else {
                        DOMAIN_NAME_TO_IP.put(host, new CopyOnWriteArrayList<>());
                        DOMAIN_NAME_TO_IP.get(host).add(ip);
                    }

                    LOG.info(String.format("Redis, 访问远程识别 Host: %s -> IP: %s, 累计缓存 IP 数量： %s", host, ip, DOMAIN_NAME_TO_IP.get(host).size()));

                    return ip;
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        }

        return null;
    }

    public static Cache useDataSource() {
        return useDataSource(CACHE_CONFIG_BEAN);
    }

    public static Cache useDataSource(CacheConfigBean cacheConfigBean) {
        if (cacheConfigBean == null)
            return useDataSource();

        if (cacheConfigBean.getHost() == null || "".equals(cacheConfigBean.getHost()))
            return useDataSource();

        String name = dataSourceName(cacheConfigBean.getHost());

        if (CACHE.containsKey(name))
            return CACHE.get(name);

        Redis.initRedis(cacheConfigBean, jedisPoolConfig);

        return CACHE.get(name);
    }

    public static <T> T call(ICallback<T> callback) throws Exception {
        return call(callback, useDataSource());
    }

    public static <T> T call(CacheConfigBean cacheConfigBean, ICallback<T> callback) throws Exception {
        return call(callback, useDataSource(cacheConfigBean));
    }

    private static <T> T call(ICallback<T> callback, Cache cache) throws Exception {
        if (callback == null)
            throw new IllegalArgumentException("ICallback<T> 参数不合法");

        if (cache == null)
            throw new IllegalArgumentException("Cache 参数不合法");

        try (Jedis jedis = cache.getResource()) {
            return callback.call(jedis);
        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
    }

    /**
     * 销毁
     */
    public static void destroy() {
        LOG.info(String.format("销毁, 当前 CACHE Jedis Pool 数量: %s", CACHE.size()));

        for (Map.Entry<String, Cache> entry : CACHE.entrySet()) {
            try {
                Cache cache = entry.getValue();
                if (cache != null)
                    cache.destroy();
            } catch (Exception e) {
                LOG.error(String.format("Jedis Pool: %s destroy 出现异常", entry.getKey()), e);
            }
        }

        DOMAIN_NAME_TO_IP.clear();
    }

    /**
     * 移除 域名-->IP 映射
     *
     * @param domainName
     * @param ip
     */
    private static void removeMapping(String domainName, String ip) {
        synchronized (LOCK) {
            if (DOMAIN_NAME_TO_IP.containsKey(domainName))
                DOMAIN_NAME_TO_IP.get(domainName).remove(ip);
        }
    }

}
