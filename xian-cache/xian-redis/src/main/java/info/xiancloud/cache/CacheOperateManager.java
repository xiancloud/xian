package info.xiancloud.cache;

/**
 * 缓存管理操作类
 * 注意: CRS长连接若30分钟无请求会自动断开, 请在业务中尝试重连
 *
 * @author John_zero, happyyangyuan
 */
public final class CacheOperateManager {
    /**
     * 校正过期时间
     *
     * @param timeout
     */
    public static int correctionTimeout(int timeout) {
        return timeout < -1 ? -1 : timeout;
    }

}
