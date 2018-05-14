package info.xiancloud.cache.redis.operate;

import redis.clients.jedis.Jedis;

/**
 * Server 服务器
 * http://redisdoc.com/server/index.html
 *
 * @author John_zero, happyyangyuan
 */
public final class ServerOperate {

    /**
     * https://redis.io/commands/info
     *
     * @param jedis   jedis object
     * @param section server, clients, memory, persistence, stats, replication, cpu, commandstats, cluster, keyspace
     * @return info string
     */
    public static String info(Jedis jedis, String section) {
        if (section == null || "".equals(section))
            return jedis.info();
        else
            return jedis.info(section);
    }

    /**
     * 根据 info 获取具体属性的值
     *
     * @param info      info
     * @param attribute attribute name
     * @return attribute in info
     */
    public static String getAttributeInInfo(String info, String attribute) {
        if (info == null || "".equals(info))
            return null;
        if (attribute == null || "".equals(attribute))
            return null;

        String[] infos = info.split("\r\n");

        for (String _info : infos) {
            if (_info.startsWith(attribute)) {
                String[] keyValue = _info.split(":");
                if (keyValue[0].equals(attribute))
                    return keyValue[1];
            }
        }

        return null;
    }

}
