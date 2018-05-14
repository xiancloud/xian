package info.xiancloud.redis.api;

import info.xiancloud.core.support.cache.api.CacheSetUtil;
import io.reactivex.functions.Consumer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class SetApi {

    @Before
    public void initialize() {

    }

    @After
    public void finish() {

    }

    @Test
    public void exists() {
        CacheSetUtil.exists("SET_API", "exists_0").subscribe((Consumer<Boolean>) Assert::assertFalse);

    }

    @Test
    public void add() {
        CacheSetUtil
                .add("SET_API", "add_0")
                .subscribe(aLong -> Assert.assertEquals(1, aLong.longValue()));

    }

    @Test
    public void adds() {
        Set<String> values = new HashSet<>();
        values.add("add_1");
        values.add("add_2");
        values.add("add_3");
        values.add("add_4");
        values.add("add_5");
        CacheSetUtil.addAll("SET_API", values).subscribe(adds -> {
            Assert.assertEquals(values.size(), adds.longValue());
        });

    }

    @Test
    public void values() {
        CacheSetUtil.values("SET_API").subscribe(values -> {
            Assert.assertNotNull(values);
            System.out.println("values.size: " + values.size());
            for (String value : values)
                System.out.println(value);
        });
    }

    @Test
    public void remove() {
        CacheSetUtil.add("SET_API", "remove_0");
        CacheSetUtil.add("SET_API", "remove_0");
        CacheSetUtil.add("SET_API", "remove_1");
        CacheSetUtil.add("SET_API", "remove_2");

        CacheSetUtil.remove("SET_API", "remove_0")
                .subscribe(remove -> Assert.assertEquals(1, remove.longValue()));
    }

    @Test
    public void removes() {
        Set<String> values = new HashSet<>();
        values.add("removes_10");
        values.add("removes_11");
        values.add("removes_12");
        values.add("removes_13");
        CacheSetUtil.addAll("SET_API", values);

        CacheSetUtil.removes("SET_API", values)
                .subscribe(removes -> Assert.assertEquals(values.size(), removes.longValue()));
    }

}
