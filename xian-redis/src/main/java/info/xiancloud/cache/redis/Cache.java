package info.xiancloud.cache.redis;

import info.xiancloud.core.util.LOG;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

public class Cache {
    private final String name;

    private final JedisPool jedisPool;

    private final int dbIndex;

    public Cache(String name, String host, int port, int timeOut, String password, int dbIndex, JedisPoolConfig jedisPoolConfig) {
        this.name = name;

        if (port < 0 || port > 65536)
            throw new IllegalArgumentException(String.format("端口: %s, 为非法参数, 取值范围为 0-65536", port));
        if (dbIndex < 0 || dbIndex > 15)
            throw new IllegalArgumentException(String.format("DB Index: %s, 为非法参数, 取值范围为 0-15", dbIndex));

        LOG.info(String.format(this.name + ", Redis 初始化入参: host: %s, port: %s, dbIndex: %s", host, port, dbIndex));

        if (password != null && !"".equals(password))
            this.jedisPool = new JedisPool(jedisPoolConfig, host, port, timeOut, password);
        else
            this.jedisPool = new JedisPool(jedisPoolConfig, host, port, timeOut);

        LOG.info(String.format(this.name + ", Redis 初始化成功, TIME_OUT: %s", timeOut));

        this.dbIndex = dbIndex;

        /**
         * 健康检查
         */
        highAvailable();
    }

    public String getName() {
        return name;
    }

    /**
     * 获取 Jedis
     *
     * @return
     */
    public Jedis getResource() {
        return getResource(this.dbIndex);
    }

    /**
     * 获取 Jedis
     *
     * @return
     */
    public Jedis getResource(int DBIndex) {
        try {
            if (this.jedisPool == null)
                throw new NullPointerException(this.name + ", Jedis Pool 未初始化");

            Jedis jedis = this.jedisPool.getResource();

            jedis.select(DBIndex);

            return jedis;
        } catch (JedisConnectionException e) {
            LOG.error(this.name + ", Jedis Pool 连接出现异常", e);
            throw e;
        } catch (JedisException e) {
            LOG.error(this.name + ", Jedis Pool 出现异常", e);
            throw e;
        } catch (Exception e) {
            LOG.error(this.name + ", Jedis Pool 获取连接, 暂时无法识别异常", e);
            throw e;
        }
    }

    /**
     * 监控检查
     *
     * @return
     */
    public boolean highAvailable() {
        try (Jedis jedis = getResource()) {
            String reply = jedis.ping();
            if ("PONG".equals(reply))
                return true;
        } catch (Exception e) {
            LOG.error(e);
        }

        return false;
    }

    /**
     * 销毁
     */
    public void destroy() {
        try {
            if (this.jedisPool != null && !this.jedisPool.isClosed())
                this.jedisPool.destroy();

            LOG.info(this.name + ", Redis 销毁成功");
        } catch (JedisException e) {
            LOG.error(String.format("Jedis Pool: %s destroy 出现异常", this.name), e);
            throw e;
        }
    }

    public int getNumActive() {
        if (this.jedisPool == null)
            return -1;

        return this.jedisPool.getNumActive();
    }

    public int getNumIdle() {
        if (this.jedisPool == null)
            return -1;

        return this.jedisPool.getNumIdle();
    }

    public int getNumWaiters() {
        if (this.jedisPool == null)
            return -1;

        return this.jedisPool.getNumWaiters();
    }

}
