package info.xiancloud.cache.service;

import info.xiancloud.core.Group;

/**
 * Cache service group.
 * <p>
 * See:
 * http://doc.redisfans.com/index.html
 */
public class CacheGroup implements Group {

    public static final String CODE_NOT_SUPPORT = "CODE_NOT_SUPPORTED";
    public static final String CODE_NOT_CACHED = "CODE_NOT_CACHED";
    public static Group singleton = new CacheGroup();

    @Override
    public String getName() {
        return "cache";
    }

    @Override
    public String getDescription() {
        return "缓存服务-Redis";
    }

}