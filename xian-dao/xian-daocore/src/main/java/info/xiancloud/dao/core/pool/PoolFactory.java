package info.xiancloud.dao.core.pool;

import info.xiancloud.core.util.Reflection;

/**
 * Pool factory.
 * this is the only way for you to get a connection.
 *
 * @author happyyangyuan
 */
public class PoolFactory {

    private static IPool POOL;

    static {
        // better performance than lasy-initialization but with a slower bootstrap.
        try {
            POOL = Reflection.getSubClassInstances(IPool.class).get(0);
            POOL.initPoolIfNot();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    /**
     * lazy-init
     *
     * @return initialized pool
     */
    public static IPool getPool() {
        return POOL;
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

}
