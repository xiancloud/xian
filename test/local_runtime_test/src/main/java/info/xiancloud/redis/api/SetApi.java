package info.xiancloud.redis.api;

import info.xiancloud.core.support.cache.api.CacheSetUtil;
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
        boolean exists = CacheSetUtil.exists("SET_API", "exists_0");
        Assert.assertFalse(exists);
    }

    @Test
    public void add() {
        long add = CacheSetUtil.add("SET_API", "add_0");
        Assert.assertEquals(1, add);
    }

    @Test
    public void adds() {
        Set<String> values = new HashSet<>();
        values.add("add_1");
        values.add("add_2");
        values.add("add_3");
        values.add("add_4");
        values.add("add_5");
        long adds = CacheSetUtil.adds("SET_API", values);
        Assert.assertEquals(values.size(), adds);
    }

    @Test
    public void values() {
        Set<String> values = CacheSetUtil.values("SET_API");
//        Set<String> values = CacheSetUtil.values("SET_API", String.class);

        Assert.assertNotNull(values);

        if (values != null) {
            System.out.println("values.size: " + values.size());
            for (String value : values)
                System.out.println(value);
        }
    }

    @Test
    public void remove() {
        CacheSetUtil.add("SET_API", "remove_0");
        CacheSetUtil.add("SET_API", "remove_0");
        CacheSetUtil.add("SET_API", "remove_1");
        CacheSetUtil.add("SET_API", "remove_2");

        long remove = CacheSetUtil.remove("SET_API", "remove_0");

        Assert.assertEquals(1, remove);
    }

    @Test
    public void removes() {
        Set<String> values = new HashSet<>();
        values.add("removes_10");
        values.add("removes_11");
        values.add("removes_12");
        values.add("removes_13");
        CacheSetUtil.adds("SET_API", values);

        long removes = CacheSetUtil.removes("SET_API", values);

        Assert.assertEquals(values.size(), removes);
    }

}
