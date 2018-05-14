package info.xiancloud.redis.api;

import info.xiancloud.core.support.cache.api.CacheListUtil;
import io.reactivex.functions.Consumer;
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
        CacheListUtil.exists("LIST_API").subscribe((Consumer<Boolean>) Assert::assertFalse);
    }

    @Test
    public void length() {
        CacheListUtil.length("LIST_API").subscribe(length -> {
            Assert.assertTrue(length > 0);
        });
    }

    @Test
    public void isEmpty() {
        CacheListUtil.isEmpty("LIST_API")
                .subscribe((Consumer<Boolean>) Assert::assertTrue);
    }

    @Test
    public void addHead() {
        CacheListUtil.addFirst("LIST_API", "addHead_0")
                .subscribe((Consumer<Boolean>) Assert::assertTrue);
    }

    @Test
    public void add() {
        CacheListUtil.add("LIST_API", "add_10")
                .subscribe((Consumer<Boolean>) Assert::assertTrue);
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
        CacheListUtil.addAll("LIST_API", lists).subscribe((Consumer<Boolean>) Assert::assertTrue);
    }

    @Test
    public void set() {
        CacheListUtil.set("LIST_API", 0, "set_0").subscribe((Consumer<Boolean>) Assert::assertTrue);
    }

    @Test
    public void remove() {
        CacheListUtil.add("LIST_API", "remove_0").subscribe((Consumer<Boolean>) Assert::assertTrue);
        CacheListUtil.remove("LIST_API", "remove_0").subscribe(remove -> {
            Assert.assertTrue(remove == 1);
        });
    }

    @Test
    public void clear() {
        CacheListUtil.clear("LIST_API").subscribe();

    }

    @Test
    public void delete() {
        CacheListUtil.delete("LIST_API").subscribe((Consumer<Boolean>) Assert::assertTrue);
    }

    @Test
    public void getAll() {
        CacheListUtil.getAll("LIST_API").subscribe(values -> {
            Assert.assertNotNull(values);
            System.out.println("values.size: " + values.size());
            for (String value : values)
                System.out.println(value);
        });
    }

    @Test
    public void getRange() {
        CacheListUtil.getRange("LIST_API", String.class)
                .subscribe(values -> {
                    Assert.assertNotNull(values);
                    System.out.println("values.size: " + values.size());
                    for (String value : values)
                        System.out.println(value);
                });

    }

    @Test
    public void get() {
        CacheListUtil.get("LIST_API", 0, String.class)
                .subscribe(value -> {
                    Assert.assertNotNull(value);
                    System.out.println(value);
                });
    }

}
