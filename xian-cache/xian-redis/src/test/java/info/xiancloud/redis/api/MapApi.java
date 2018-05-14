package info.xiancloud.redis.api;

import info.xiancloud.core.support.cache.api.CacheMapUtil;
import io.reactivex.functions.Consumer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapApi {

    @Before
    public void initialize() {

    }

    @After
    public void finish() {

    }

    @Test
    public void exists() {
        CacheMapUtil.exists("MAP_API")
                .subscribe((Consumer<Boolean>) Assert::assertFalse);
    }

    @Test
    public void containsKey() {
        CacheMapUtil.containsKey("MAP_API", "MAP")
                .subscribe((Consumer<Boolean>) Assert::assertFalse);
    }

    @Test
    public void size() {
        CacheMapUtil.size("MAP_API").subscribe(size -> Assert.assertTrue(size >= 0));
    }

    @Test
    public void isEmpty() {
        CacheMapUtil.isEmpty("MAP_API").subscribe(System.out::println);
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

        CacheMapUtil.get("MAP_API", "put_key_0", String.class)
                .subscribe(value -> {
                    Assert.assertEquals("put_value_0", value);
                });
    }

    @Test
    public void getAll() {
        CacheMapUtil.getAll("MAP_API", String.class, String.class)
                .subscribe(maps -> {
                    for (Map.Entry<String, String> entry : maps.entrySet()) {
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                    }
                });
    }

    @Test
    public void remove() {
        CacheMapUtil.put("MAP_API", "put_key_0", "put_value_0");

        CacheMapUtil.remove("MAP_API", "put_key_0")
                .subscribe(remove -> {
                    System.out.println(remove);
                    Assert.assertTrue(remove);
                });

        CacheMapUtil.containsKey("MAP_API", "put_key_0")
                .subscribe(containsKey -> {
                    System.out.println(containsKey);
                    Assert.assertFalse(containsKey);
                });
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
        CacheMapUtil.clear("MAP_API").subscribe((Consumer<Boolean>) Assert::assertTrue);

        CacheMapUtil.size("MAP_API").subscribe(size -> Assert.assertTrue(size >= 0));
    }

    @Test
    public void keys() {
        CacheMapUtil.keys("MAP_API", String.class)
                .subscribe(keys -> {
                    for (String key : keys) {
                        System.out.println(key);
                    }
                });
    }

    @Test
    public void values() {
        CacheMapUtil.values("MAP_API", String.class)
                .subscribe(values -> {
                    for (String value : values) {
                        System.out.println(value);
                    }
                });
    }

}
