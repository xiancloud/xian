package info.xiancloud.redis.api;

import info.xiancloud.core.support.cache.api.CacheObjectUtil;
import io.reactivex.functions.Consumer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ObjectApi {

    @Before
    public void initialize() {

    }

    @After
    public void finish() {

    }

    @Test
    public void keys() {
        CacheObjectUtil.keys("*")
                .subscribe(keys -> {
                    Assert.assertNotNull(keys);
                    if (!keys.isEmpty()) {
                        for (String key : keys)
                            System.out.println(key);
                    }
                });
    }

    @Test
    public void exists() {
        CacheObjectUtil.exists("exists_key").subscribe((Consumer<Boolean>) Assert::assertFalse);
    }

    @Test
    public void set() {
        CacheObjectUtil.set("object_set", "set").subscribe();
        CacheObjectUtil.set("object_set", "set", 300).subscribe();
    }

    @Test
    public void get() {
        CacheObjectUtil.get("object_set", String.class).subscribe(System.out::println);
    }

    @Test
    public void remove() {
        CacheObjectUtil.remove("object_set").subscribe((Consumer<Boolean>) Assert::assertTrue);
    }

    @Test
    public void increment() {
        CacheObjectUtil.increment("increment")
                .subscribe(System.out::println);

        CacheObjectUtil.incrementByValue("increment_by_value", 100, 5)
                .subscribe(System.out::println);

        try {
            Thread.sleep(6 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CacheObjectUtil.get("increment_by_value", Long.class)
                .subscribe(System.out::println);
    }

    @Test
    public void decrement() {
        CacheObjectUtil.decrement("decrement")
                .subscribe(System.out::println);

        CacheObjectUtil.decrementByValue("decrement_by_value", 1, 5)
                .subscribe(System.out::println);

        try {
            Thread.sleep(6 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CacheObjectUtil.get("decrement_by_value", Long.class)
                .subscribe(System.out::println);
    }

    @Test
    public void ttl() {
        CacheObjectUtil.ttl(null, "object_set")
                .subscribe(System.out::println);
    }

    @Test
    public void type() {
        CacheObjectUtil.type(null, "object_set")
                .subscribe(System.out::println);
    }

}
