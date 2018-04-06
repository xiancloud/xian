package info.xiancloud.redis.api;

import info.xiancloud.core.support.cache.api.CacheListUtil;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

@FixMethodOrder(MethodSorters.JVM)
public class ListApi {

    @Before
    public void initialize() {
        CacheListUtil.delete("LIST_API");
    }

    @After
    public void finish() {

    }

    @Test
    public void exists() {
        boolean exists = CacheListUtil.exists("LIST_API");
        Assert.assertFalse(exists);
    }

    @Test
    public void length() {
        long length = CacheListUtil.length("LIST_API");
        assert length > 0;
    }

    @Test
    public void isEmpty() {
        boolean isEmpty = CacheListUtil.isEmpty("LIST_API");
        Assert.assertTrue(isEmpty);
    }

    @Test
    public void addHead() {
        boolean addHead = CacheListUtil.addFirst("LIST_API", "addHead_0");
        Assert.assertTrue(addHead);
    }

    @Test
    public void add() {
        boolean add = CacheListUtil.add("LIST_API", "add_10");
        Assert.assertTrue(add);
    }

    /**
     * LRANGE LIST_API 0 -1
     */
    @Test
    public void addAll() {
        List<String> lists = new ArrayList<>();
        lists.add("lists_0");
        lists.add("lists_1");
        lists.add("lists_2");
        lists.add("lists_3");
        boolean addAll = CacheListUtil.addAll("LIST_API", lists);
        Assert.assertTrue(addAll);
    }

    @Test
    public void set() {
        boolean set = CacheListUtil.set("LIST_API", 0, "set_0");
        Assert.assertTrue(set);
    }

    @Test
    public void remove() {
        boolean add = CacheListUtil.add("LIST_API", "remove_0");
        Assert.assertTrue(add);

        boolean remove = CacheListUtil.remove("LIST_API", "remove_0");
        Assert.assertTrue(remove);
    }

    @Test
    public void clear() {
        boolean clear = CacheListUtil.clear("LIST_API");
        Assert.assertTrue(clear);
    }

    @Test
    public void delete() {
        boolean delete = CacheListUtil.delete("LIST_API");
        Assert.assertTrue(delete);
    }

    @Test
    public void getAll() {
        List<String> values = CacheListUtil.getAll("LIST_API");

        Assert.assertNotNull(values);

        if (values != null) {
            System.out.println("values.size: " + values.size());

            for (String value : values)
                System.out.println(value);
        }
    }

    @Test
    public void getRange() {
        List<String> values = CacheListUtil.getRange("LIST_API", String.class);
//        List<String> values = CacheListUtil.getRange("LIST_API", 0, -1, String.class);

        Assert.assertNotNull(values);

        if (values != null) {
            System.out.println("values.size: " + values.size());

            for (String value : values)
                System.out.println(value);
        }
    }

    @Test
    public void get() {
        String value = CacheListUtil.get("LIST_API", 0, String.class);
        Assert.assertNotNull(value);

        if (value != null)
            System.out.println(value);
    }

}
