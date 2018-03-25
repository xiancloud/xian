package info.xiancloud.redis.api;

import info.xiancloud.core.support.cache.api.CacheMapUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class MapApi {

    @Before
    public void initialize() {

    }

    @After
    public void finish() {

    }

    @Test
    public void exists() {
        boolean exists = CacheMapUtil.exists("MAP_API");
        Assert.assertFalse(exists);
    }

    @Test
    public void containsKey() {
        boolean containsKey = CacheMapUtil.containsKey("MAP_API", "MAP");
        Assert.assertFalse(containsKey);
    }

    @Test
    public void size() {
        long size = CacheMapUtil.size("MAP_API");
        assert size >= 0;
    }

    @Test
    public void isEmpty() {
        boolean isEmpty = CacheMapUtil.isEmpty("MAP_API");

        System.out.println(isEmpty);
    }

    @Test
    public void put() {
        CacheMapUtil.put("MAP_API", "put_key_0", "put_value_0");
    }

    @Test
    public void putAll() {
        Map<String, String> maps = new HashMap<>();
        maps.put("put_key_0", "put_value_0");
        maps.put("put_key_1", "put_value_1");
        maps.put("put_key_2", "put_value_2");
        maps.put("put_key_3", "put_value_3");

        CacheMapUtil.putAll("AMP_API", maps);
    }

    @Test
    public void get() {
        CacheMapUtil.put("MAP_API", "put_key_0", "put_value_0");

        String value = CacheMapUtil.get("MAP_API", "put_key_0", String.class);

        Assert.assertEquals("put_value_0", value);
    }

    @Test
    public void getAll() {
        Map<String, String> maps = CacheMapUtil.getAll("MAP_API", String.class, String.class);
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    @Test
    public void remove() {
        CacheMapUtil.put("MAP_API", "put_key_0", "put_value_0");

        boolean remove = CacheMapUtil.remove("MAP_API", "put_key_0");
        System.out.println(remove);
        Assert.assertTrue(remove);

        boolean containsKey = CacheMapUtil.containsKey("MAP_API", "put_key_0");
        System.out.println(containsKey);
        Assert.assertFalse(containsKey);
    }

    @Test
    public void batchRemove() {
        Map<String, List<String>> batchRemoves = new HashMap<>();
        List<String> smallKeys = new ArrayList<>();
        smallKeys.add("put_key_0");
        smallKeys.add("put_key_1");
        batchRemoves.put("MAP_API", smallKeys);

        CacheMapUtil.batchRemove(batchRemoves);
    }

    @Test
    public void clear() {
        boolean clear = CacheMapUtil.clear("MAP_API");
        Assert.assertTrue(clear);

        long size = CacheMapUtil.size("MAP_API");
        assert size >= 0;
    }

    @Test
    public void keys() {
        Set<String> keys = CacheMapUtil.keys("MAP_API", String.class);
        for (String key : keys) {
            System.out.println(key);
        }
    }

    @Test
    public void values() {
        List<String> values = CacheMapUtil.values("MAP_API", String.class);
        for (String value : values) {
            System.out.println(value);
        }
    }

}
