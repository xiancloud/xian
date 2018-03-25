package info.xiancloud.redis.api;

import info.xiancloud.core.support.cache.api.CacheObjectUtil;
import info.xiancloud.core.support.cache.vo.ScanVo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class ObjectApi {

    @Before
    public void initialize() {

    }

    @After
    public void finish() {

    }

    @Test
    public void keys() {
        Set<String> keys = CacheObjectUtil.keys("*");

        Assert.assertNotNull(keys);

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys)
                System.out.println(key);
        }
    }

    @Test
    public void exists() {
        boolean exists = CacheObjectUtil.exists("exists_key");
        Assert.assertFalse(exists);
    }

    @Test
    public void set() {
        boolean set = CacheObjectUtil.set("object_set", "set");
        Assert.assertTrue(set);

        boolean _set = CacheObjectUtil.set("object_set", "set", 300);
        Assert.assertTrue(_set);
    }

    @Test
    public void get() {
        String value = CacheObjectUtil.get("object_set", String.class);

        System.out.println(value);
    }

    @Test
    public void remove() {
        boolean remove = CacheObjectUtil.remove("object_set");

        Assert.assertTrue(remove);
    }

    @Test
    public void increment() {
        long increment = CacheObjectUtil.increment("increment");
        System.out.println(increment);

        long increment_by_value = CacheObjectUtil.incrementByValue("increment_by_value", 100, 5);
        System.out.println(increment_by_value);

        try {
            Thread.sleep(6 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long _increment_by_value = CacheObjectUtil.get("increment_by_value", Long.class);
        System.out.println(_increment_by_value);
    }

    @Test
    public void decrement() {
        long decrement = CacheObjectUtil.decrement("decrement");
        System.out.println(decrement);

        long decrement_by_value = CacheObjectUtil.decrementByValue("decrement_by_value", 1, 5);
        System.out.println(decrement_by_value);

        try {
            Thread.sleep(6 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long _decrement_by_value = CacheObjectUtil.get("decrement_by_value", Long.class);
        System.out.println(_decrement_by_value);
    }

    @Test
    public void ttl() {
        long ttl = CacheObjectUtil.ttl(null, "object_set");
        System.out.println(ttl);
    }

    @Test
    public void type() {
        String type = CacheObjectUtil.type(null, "object_set");
        System.out.println(type);
    }

    @Test
    public void scan() {
        String pattern = "*";
        int count = 10;
        String cursor = ScanVo.CURSOR_START_END;

        ScanVo scanVo = CacheObjectUtil.scan(pattern, count, cursor);
        while (scanVo != null) {
            List<String> keys = scanVo.getResult();
            System.out.println("游标: " + scanVo.getCursor() + ", 匹配数量: " + keys.size());
            keys.stream().forEach(key -> System.out.println(key));

            if (scanVo.isEndIteration())
                return;

            System.out.println("下一轮起始游标: " + scanVo.getCursor());
            scanVo = CacheObjectUtil.scan(pattern, count, scanVo.getCursor());
        }
    }

}
