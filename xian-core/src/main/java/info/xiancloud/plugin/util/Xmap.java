package info.xiancloud.plugin.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Map operation with chained calls support.
 * This class extends {@link HashMap}
 * You can use {@link com.alibaba.fastjson.JSONObject#fluentPut(String, Object)} instead.
 *
 * @author happyyangyuan
 */
public class Xmap<K, V> extends HashMap<K, V> {

    /**
     * The same as {@link java.util.Map#put(Object, Object)}  java.util.Map.put(Key, Value)}.<br>
     * The only difference is that this method returns 'this'. In this way, we can easily use chained calls.
     * <p>
     * eg. <code>Xmap xmap = Xmap.xput("cat","Tom").xput("mouse","Jerry");</code>
     * </p>
     *
     * @return this xmap.
     */
    public Xmap xput(K key, V value) {
        put(key, value);
        return this;
    }

    /**
     * The same as {@link #get(Object)}  Map.get(key)}
     */
    public V getX(K key) {
        return get(key);
    }

    /**
     * The same as {@link java.util.Map#putAll(Map) java.util.Map.putAll(map)}.<br>
     * The only difference is that this method returns 'this'. In this way, we can easily use chained calls.
     * <p>
     * eg. <code>Xmap xmap = Xmap.create().xputAll(map).xput("cat","Tom").xput("mouse","Jerry");</code>
     * </p>
     *
     * @return this xmap.
     */
    public Xmap<K, V> xputAll(Map<K, V> map) {
        putAll(map);
        return this;
    }

    /**
     * Create a new empty Xmap.
     */
    public static <K, V> Xmap<K, V> create() {
        return new Xmap<>();
    }

    /**
     * Clone a Xmap with all entries in the parameter 'map'.
     */
    public static <K, V> Xmap<K, V> create(Map<K, V> map) {
        Xmap<K, V> xmap = new Xmap<>();
        xmap.putAll(map);
        return xmap;
    }
}
