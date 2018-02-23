package info.xiancloud.plugin.dao.core.jdbc.pool;

import info.xiancloud.plugin.dao.core.jdbc.pool.druid.DruidPool;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.ProxyBuilder;

import java.lang.reflect.Method;

/**
 * @author happyyangyuan
 */
public class PoolFactory {

    private static final String GET_CONNECTION_METHOD_NAME_REGEX = "get.*Connection";
    private static final boolean GET_CONNECTION_MONITOR_ENABLED = false;
    /**
     * 全局连接池,来自db_write/db_read配置指定的数据源
     */
    private static IPool defaultPool = GET_CONNECTION_MONITOR_ENABLED ? new ProxyBuilder<IPool>(new DruidPool()) {
        public Object before(Method method, Object[] args) throws ProxyBuilder.OriginalResultReplacement {
            if (method.getName().matches(GET_CONNECTION_METHOD_NAME_REGEX)) {
                LOG.debug("这里是记录获取连接起始时间");
                return System.nanoTime();
            }
            return null;
        }

        public void after(Method method, Object[] args, Object methodReturn, Object beforeReturn) throws OriginalResultReplacement {
            if (method.getName().matches(GET_CONNECTION_METHOD_NAME_REGEX)) {
                LOG.cost(method.getName(), (long) beforeReturn, System.nanoTime());
            }
        }
    }.getProxy() : new DruidPool();


    //获取全局数据源,lazy-create
    public static IPool getPool() {
        if (!defaultPool.isInitialized()) {
            defaultPool.initPool();
        }
        return defaultPool;
    }

    /**
     * 根据指定的域名端口和数据库名获取连接池,如果之前已经初始化过该连接池那么直接返回已存在的那个连接池
     *
     * @param host     MySQL主机ip或域名
     * @param port     MySQL端口
     * @param database 数据库名
     * @return 数据库连接池
     */
    public static IPool getPool(String host, Integer port, String database, String... userPwd) {
        //暂未启用
        throw new RuntimeException("未找到指定的数据库连接池");
    }

    public static void main(String[] args) {
        System.out.println("getWriteConnection1".matches(GET_CONNECTION_METHOD_NAME_REGEX));
    }

}
