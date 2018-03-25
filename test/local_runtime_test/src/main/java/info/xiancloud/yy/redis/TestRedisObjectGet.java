package info.xiancloud.yy.redis;

import info.xiancloud.core.support.cache.api.CacheObjectUtil;

/**
 * @author happyyangyuan
 */
public class TestRedisObjectGet {
    public static void main(String[] args) {
        System.out.println(CacheObjectUtil.get("123", String.class));
    }
}
