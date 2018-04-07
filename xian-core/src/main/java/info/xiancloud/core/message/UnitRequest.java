package info.xiancloud.core.message;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;
import info.xiancloud.core.util.Reflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unit's parameter wrapper class, a map is contained in this class.
 * Note that this class is not thread-safe, and we use it only under synchronous situation in the xian framework.
 * <p>
 * Please compare this 'UnitRequest' terminology to 'ServletRequest',
 * UnitRequest is request bean argument for for Xian framework's Unit,
 * while ServletRequest is request bean argument for java servlet.
 * And the same to {@link UnitResponse}
 *
 * @author happyyangyuan
 */
final public class UnitRequest {

    private Map<String, Object> argMap;
    private RequestContext context;

    /**
     * Use the default constructor carefully.
     */
    public UnitRequest() {
    }

    public RequestContext getContext() {
        if (context == null) context = RequestContext.create();
        return context;
    }

    public UnitRequest setContext(RequestContext context) {
        this.context = context;
        return this;
    }

    public UnitRequest(Map<String, Object> argMap) {
        setArgMap(argMap);
    }

    public UnitRequest setArgMap(Map<String, Object> argMap) {
        this.argMap = argMap;
        return this;
    }

    public Map<String, Object> getArgMap() {
        if (argMap == null)
            argMap = new HashMap<>();//avoid a null pointer exception.
        return argMap;
    }

    /**
     * convert the {@linkplain #argMap} into json object.
     * Note that, this method must not be a getter or it will be used in serialization which we don't want it be.
     *
     * @return the converted json object.
     */
    public JSONObject argJson() {
        return Reflection.toType(argMap, JSONObject.class);
    }

    /**
     * @return 读取map内指定参数, 如果不存在key，或者key的value是null，那么返回空。
     */
    public <T> T get(String key, Class<T> tClass) {
        return Reflection.toType(argMap.get(key), tClass);
    }

    /**
     * Get the specified key's value from the unit input, anc cast it into the type you want.
     *
     * @return read value from the argMap, if the key doesn't exist or the key's value is null，then the defaultValue is returned.
     */
    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        Object obj = argMap.get(key);
        if (obj == null) {
            return defaultValue;
        }
        return Reflection.toType(obj, clazz);
    }

    /**
     * 如果不需要将value强行转换为String，可以使用更简便的 {@link #get(String) 返回泛型的get方法}
     */
    public String getString(String key) {
        return get(key, String.class);
    }

    /**
     * 如果不需要将value强行转换为String，可以使用更简便的 {@link #get(String) 返回泛型的get方法}
     */
    public String getString(String key, String defaultValue) {
        return get(key, String.class, defaultValue);
    }

    /**
     * 从unit的入参argMap内get元素取值，返回类型为泛型；
     * 注意，此方法仅仅是为了方便使用，不能保证类型转换不出问题，需要调用者来保证类型转换正确性。
     *
     * @throws ClassCastException 类型转换异常
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) argMap.get(key);
    }

    /**
     * 从unit的入参argMap内get元素取值，如果不存在取值，那么返回给定的默认值，返回类型为泛型；
     * 注意，此方法仅仅是为了方便使用，不能保证类型转换不出问题，需要调用者来保证类型转换正确性。
     *
     * @throws ClassCastException 类型转换异常
     */
    public <T> T get(String key, T defaultValue) {
        T value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * @param key   key
     * @param clazz 类
     * @param <T>   list的元素类型
     * @return list
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        return Reflection.toTypedList(get(key), clazz);
    }

    /**
     * {@link #getList(String, Class)}的快捷方式，不过需要你自己注意类型转换异常
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key) {
        return Reflection.toType(get(key), List.class);
    }

    /**
     * @param key          the argument name
     * @param elementClass the element class
     * @param <T>          the generic type
     * @return a newly created hash set which contains all the elements for the given key.
     */
    public <T> Set<T> getSet(String key, Class<T> elementClass) {
        return Reflection.toTypedSet(get(key), elementClass);
    }

    /**
     * Get the set. Note you must be sure with the generic type, or a class cast exception will be thrown.
     *
     * @param key the argument name
     * @param <T> the generic type
     * @return the set representing the argument.
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> getSet(String key) {
        return Reflection.toType(get(key), Set.class);
    }

    /**
     * Convert this unit request's argument map into java bean.
     *
     * @param beanClass the bean type that you want to
     * @return the casted java bean object.
     */
    public <T> T getArgBean(Class<? extends T> beanClass) {
        return Reflection.toType(argMap, beanClass);
    }

    /**
     * create a new unit request instance.
     *
     * @return the newly created unit request instance.
     */
    public static UnitRequest create() {
        return new UnitRequest();
    }

    /**
     * Create a new UnitRequest instance with the group and unit name.
     */
    public static UnitRequest create(String group, String unit) {
        return new UnitRequest().setContext(RequestContext.create().setGroup(group).setUnit(unit));
    }

    /**
     * Create a new UnitRequest instance by the Unit class.
     */
    public static UnitRequest create(Class<? extends Unit> unitClass) {
        Unit unit = LocalUnitsManager.getUnitByUnitClass(unitClass);
        return create(unit.getGroup().getName(), unit.getName());
    }

}