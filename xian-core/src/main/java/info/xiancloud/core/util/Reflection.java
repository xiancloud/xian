package info.xiancloud.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Defaults;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import info.xiancloud.core.Group;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Reflection utility class. You will love it, love using it.
 *
 * @author happyyangyuan
 */
public class Reflection {

    /**
     * Scan for all subclasses of the given parent class/interface.<br>
     * Please pay attention that this method is used by {@link LOG}'s static field initialization, which means we are in danger of class loading deadlock.
     * So we cannot use {@link LOG  LOG util class} in this method.
     *
     * @return a list of subclass instances or empty array list if no subclass is found.
     */
    public static <T> List<T> getSubClassInstances(Class<T> tClass) {
        List<T> instances = new ArrayList<T>() {
            {
                addAll(TraverseClasspath.getSubclassInstances(tClass));
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < this.size(); i++) {
                    stringBuilder.append(get(i).getClass().getSimpleName()).append(",");
                }
                return stringBuilder.toString();
            }
        };
        return instances;
    }

    /**
     * Scan and get classes under certain annotation, and initiate them.
     */
    public static <T> List<T> getWithAnnotatedClass(Class annotationClass, String packages) {
        List instances = new ArrayList() {{
            addAll(TraverseClasspath.getWithAnnotatedClass(annotationClass, packages));
        }};
        return instances;
    }

    /**
     * <p>
     * get super class's generic types.
     * eg. A extends B(T1,T2)  will return [T1,T2].<br>
     * if no generic type is defined for the parent class, null is returned.
     * It is not recommended to use this method when you want to get a {@link Unit unit}'s
     * {@link Group group}. Instead, use {@link LocalUnitsManager}.
     * </p>
     */
    public static Type[] getSupperGenericType(Object object) {
        if (Proxy.isProxyClass(object.getClass())) {
            object = ProxyBuilder.getProxyBuilder(object.hashCode()).getMostOriginalObject();
        }
        return getSupperGenericType(object.getClass());
    }

    /**
     * Get super class's  generic types. eg. A extends B(T1,T2)  will return [T1,T2]<br>
     * If no generic type is defined for the super class, then null is returned.
     */
    public static Type[] getSupperGenericType(Class clazz) {
        ParameterizedType parameterizedType;
        if (clazz.getGenericInterfaces().length > 0) {//父类是interface, the super type is an interface.
            if (clazz.getGenericInterfaces()[0] instanceof ParameterizedType) {
                /*注意：有多个父接口时，这里是取第一个父接口
                  Attention: if multiple parents are defined, we only get the first one.*/
                parameterizedType = (ParameterizedType) clazz.getGenericInterfaces()[0];
            } else {
                return null;
            }
        } else {//父类是class, the super type is a class.
            if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
                parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
            } else {
                return null;
            }
        }
        return parameterizedType.getActualTypeArguments();
    }

    /**
     * <p>judge the given data object is instance of the given type or not.</p>
     *
     * @param data the data object.
     * @param type the given class to test.
     * @return true or false as described.
     */
    public static boolean instanceOf(Object data, Class type) {
        //这里有一个事实,即参数data必定是非原生类型,即使传入时是原生类型,它也被java强制转化为Object非原生了.
        //here we rely on the jdk default behavior of packing primitive type parameter into packed type,
        //which means that if you passe an int to the data parameter, right here within this method,
        //you will always get an Integer object.
        if (type.isInstance(data)) {
            return true;
        }
        if (data == null) {
            return false;
        }
        if (type.isPrimitive()) {
            if (type == int.class) {
                type = Integer.class;
            } else if (type == long.class) {
                type = Long.class;
            } else if (type == short.class) {
                type = Short.class;
            } else if (type == double.class) {
                type = Double.class;
            } else if (type == float.class) {
                type = Float.class;
            } else if (type == char.class) {
                type = Character.class;
            } else if (type == byte.class) {
                type = Byte.class;
            } else if (type == boolean.class) {
                type = Boolean.class;
            }
        }
        return type.isInstance(data);
    }

    /**
     * getPrimitiveType of the given class
     *
     * @param type the given class.
     * @return the primitive of the given class, eg. Integer/int to int, Double/double to double etc...
     * @throws IllegalArgumentException if the given class can not be unpacked to primitive type.
     */
    public static Class getPrimitiveType(Class type) {
        if (type == int.class || type == Integer.class) {
            return int.class;
        } else if (type == long.class || type == Long.class) {
            return long.class;
        } else if (type == short.class || type == Short.class) {
            return short.class;
        } else if (type == double.class || type == Double.class) {
            return double.class;
        } else if (type == float.class || type == Float.class) {
            return float.class;
        } else if (type == char.class || type == Character.class) {
            return char.class;
        } else if (type == byte.class || type == Byte.class) {
            return byte.class;
        } else if (type == boolean.class || type == Boolean.class) {
            return boolean.class;
        }
        throw new IllegalArgumentException("Type '" + type + "' can not be converted to primitive type.");
    }

    /**
     * make a forced class casting.
     *
     * @param obj   the object you want to cast from.
     * @param clazz the class your want to cast to.
     * @return The casted object.
     */
    private static <T> T cast(Object obj, Class<T> clazz) {
        if (obj == null)
            return null;
        if (clazz == null)
            throw new IllegalArgumentException("parameter 'Class<T> clazz' is not allowed to be null.");
        if (clazz.isEnum() && obj instanceof String) {
            Class<Enum> enumClazz = (Class<Enum>) clazz;
            return (T) Enum.valueOf(enumClazz, obj.toString());
        }
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE)
                clazz = (Class<T>) Integer.class;
            else if (clazz == Long.TYPE)
                clazz = (Class<T>) Long.class;
            else if (clazz == Short.TYPE)
                clazz = (Class<T>) Short.class;
            else if (clazz == Double.TYPE)
                clazz = (Class<T>) Double.class;
            else if (clazz == Float.TYPE)
                clazz = (Class<T>) Float.class;
            else if (clazz == Character.TYPE)
                clazz = (Class<T>) Character.class;
            else if (clazz == Byte.TYPE)
                clazz = (Class<T>) Byte.class;
            else if (clazz == Boolean.TYPE)
                clazz = (Class<T>) Boolean.class;
        }
        return clazz.cast(obj);
    }

    /**
     * 将data适配为指定元素类型的list，请放心大胆做转换。<br>
     * adapt the data object to the given typed list, it is same to make a cast even if the given data is not a list,
     * or the element is not the given type.
     */
    public static <T> List<T> toTypedList(Object data, Class<T> type) {
        List<T> listResult = new ArrayList<>();
        if (data == null) {
            return listResult;
        }
        if (data instanceof Collection) {
            Collection collection = (Collection) data;
            for (Object o : collection) {
                listResult.add(Reflection.toType(o, type));
            }
            return listResult;
        }
        if (data.getClass().isArray()) {
            for (Object o : ArrayUtil.toObjectArray(data)) {
                listResult.add(Reflection.toType(o, type));
            }
            return listResult;
        }
        listResult.add(Reflection.toType(data, type));
        return listResult;
    }

    /**
     * <p>
     * This is a flexible tolerable type casting method with the help of serializing tools (fastjson currently).
     * </p>
     * Supports:<br>
     * map to jsonObject to bean <br>
     * collection ---- fetch the first element and cast it to map/json/bean<br>
     * collection --set-- HashSet<br>
     * collection --list-- ArrayList<br>
     * collection --queue-- LinkedList<br>
     */
    public static <T> T toType(Object data, Class<T> type) {
        try {
            if (Reflection.instanceOf(data, type)) {
                return Reflection.cast(data, type);
            }
            if (Collection.class.isAssignableFrom(type)) {
                return toCollectionType(data, type);
            }
            if (type.isArray()) {
                return toArray(data, type);
            }
            return toNonCollectionType(data, type);
        } catch (Throwable e) {
            throw castFailedException(data, type, e);
        }
    }

    /**
     * <p>
     * private method, annotation is ignored.
     * </p>
     * 兼容性的集合类型转换
     * 将一个未知类型转化为你想要的那个集合类型，它会宽容地做转换，比如map转成List<Map>，bean映射成List<Bean>
     * 集合数据会被转成目标类型的集合数据
     * 如果入参是null，那么返回结果就是一个空的集合
     *
     * @param collectionType 目标类型，必须是集合类型;  the destination type, must be a collection type.
     * @throws IllegalArgumentException 参数不合法; illegal argument.
     */
    private static <T> T toCollectionType(Object data, Class<T> collectionType) {
        if (!Collection.class.isAssignableFrom(collectionType)) {
            throw new IllegalArgumentException("only collection type is allowed here, but your's is: " + collectionType);
        }
        Collection result;
        try {
            result = (Collection) collectionType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            /*here the given type can not be initialized, so we make a guess*/
            if (List.class.isAssignableFrom(collectionType)) {
                result = new ArrayList();
            } else if (Set.class.isAssignableFrom(collectionType)) {
                result = new HashSet();
            } else if (Queue.class.isAssignableFrom(collectionType)) {
                result = new LinkedList();
            } else {
                throw new IllegalArgumentException("Unsupported collection type: " + collectionType);
            }
        }
        if (data == null) {
            return null;
        } else if (data instanceof Collection) {
            result.addAll((Collection) data);
            return (T) result;
        } else if (data.getClass().isArray()) {
            result.addAll(ArrayUtil.toList(data));
            return (T) result;
        } else if (data instanceof String) {
            String value = data.toString();
            try {
                JSONArray arrayValue = JSON.parseArray(value);
                result.addAll(arrayValue);
                return (T) result;
            } catch (JSONException parseFailed) {
                LOG.debug("解析数组失败，那么解析jsonObject");
                try {
                    result.add(JSON.parseObject(value));
                    return (T) result;
                } catch (JSONException parseJSONObjectFailed) {
                    LOG.debug("解析jsonObject失败，那么直接将字符串值添加到结果集合内，这里这样做比较宽容，后期评估是否适合大部分业务场景");
                    result.add(value);
                    return (T) result;
                }
            }
        } else {
            result.add(data);
            return (T) result;
        }
    }

    private static <T> T toArray(Object data, Class<T> arrayType) {
        if (!arrayType.isArray()) {
            throw new IllegalArgumentException("only array type is supported, your type is: " + arrayType);
        }
        Class componentType = arrayType.getComponentType();
        Object arrayObject;
        if (data == null) {
            return null;
        } else if (data.getClass().isArray()) {
            if (data.getClass().getComponentType().isPrimitive()) {
                if (componentType.isPrimitive())
                    return (T) data;
                arrayObject = ArrayUtil.toObjectArray(data);
            } else {
                Object[] tmp = (Object[]) data;
                arrayObject = Array.newInstance(componentType, tmp.length);
                for (int i = 0; i < tmp.length; i++) {
                    Array.set(arrayObject, i, toType(tmp[i], componentType));
                }
            }
        } else if (data instanceof Collection) {
            arrayObject = ((Collection) data).toArray();
        } else if (data instanceof String) {
            String value = data.toString();
            try {
                List list = JSON.parseArray(value, componentType);
                arrayObject = ArrayUtil.toArray(list, componentType);
            } catch (JSONException parseFailed) {
                LOG.debug("解析数组失败，那么直接将其转换为单元素数组");
                arrayObject = Array.newInstance(componentType, 1);
                Array.set(arrayObject, 0, toType(value, componentType));
            }
        } else {
            arrayObject = Array.newInstance(componentType, 1);
            Array.set(arrayObject, 0, toType(data, componentType));
        }
        return (T) arrayObject;
        /*if (componentType.isPrimitive()) {
            return (T) ArrayUtil.toPrimitiveArray(arrayObject);
        } else {
            return (T) arrayObject;
        }*/
    }

    /**
     * 具有一定兼容性的类型转换/切换
     * 将一个未知类型转化为你想要的那个类型，它会宽容地做转换，比如map映射成bean，bean映射成map
     * list的第一个元素映射成bean，list的第一个元素映射成map等等。
     * 如果入参是null，那么返回结果也是null
     *
     * @param nonCollectionType 目标类型，不允许是集合类型
     */
    private static <T> T toNonCollectionType(Object data, Class<T> nonCollectionType) {
        if (Collection.class.isAssignableFrom(nonCollectionType)) {
            throw new RuntimeException("API使用错误，本方法不支持将目标对象转为非集合类型");
        }
        if (data == null) {
            return Defaults.defaultValue(nonCollectionType);
        }
        if (data.getClass().isArray()) {
            data = ArrayUtil.toList(data);
        }
        if (data instanceof Collection) {
            Collection collection = (Collection) data;
            if (nonCollectionType == String.class) {
                return (T) JSON.toJSONString(collection);
            }
            if (collection.isEmpty()) {
                return null;
            }
            if (collection.size() >= 2) {
                LOG.warn(new Throwable(String.format("集合 【 %s 】  的元素个数不只一个,我们只取出第一个做转换", data)));
            }
            for (Object element : collection) {
                LOG.debug("集合的第一个元素会被转为目标类型");
                return transferNonCollection(element, nonCollectionType);
            }
        } else {
            return transferNonCollection(data, nonCollectionType);
        }
        throw castFailedException(data, nonCollectionType);
    }

    private static <T> T transferNonCollection(Object nonCollectionData, Class<T> nonCollectionType) {
        if (nonCollectionData == null) {
            return Defaults.defaultValue(nonCollectionType);
        }
        if (Reflection.instanceOf(nonCollectionData, nonCollectionType)) {
            return Reflection.cast(nonCollectionData, nonCollectionType);
        }
        if (nonCollectionData instanceof String) {
            String stringData = nonCollectionData.toString();
            if (nonCollectionType == int.class || nonCollectionType == Integer.class) {
                return (T) Integer.valueOf(stringData);
            }
            if (nonCollectionType == long.class || nonCollectionType == Long.class) {
                return (T) Long.valueOf(stringData);
            }
            if (nonCollectionType == double.class || nonCollectionType == Double.class) {
                return (T) Double.valueOf(stringData);
            }
            if (nonCollectionType == float.class || nonCollectionType == Float.class) {
                return (T) Float.valueOf(stringData);
            }
            if (nonCollectionType == short.class || nonCollectionType == Short.class) {
                return (T) Short.valueOf(stringData);
            }
            if (nonCollectionType == char.class || nonCollectionType == Character.class) {
                if (stringData.length() == 1) {
                    return (T) Character.valueOf(stringData.charAt(0));
                } else {
                    throw castFailedException(stringData, nonCollectionType);
                }
            }
            if (nonCollectionType == byte.class || nonCollectionType == Byte.class) {
                return (T) Byte.decode(stringData);
            }
            if (nonCollectionType == Boolean.class || nonCollectionType == boolean.class) {
                if (stringData.trim().equalsIgnoreCase("true")) {
                    return (T) Boolean.TRUE;
                } else if (stringData.trim().equalsIgnoreCase("false")) {
                    return (T) Boolean.FALSE;
                } else {
                    throw castFailedException(stringData, nonCollectionType);
                }
            }
            if (Map.class.isAssignableFrom(nonCollectionType)) {
                Map result;
                try {
                    result = JSON.parseObject(stringData);
                } catch (JSONException e) {
                    throw castFailedException(stringData, nonCollectionType, e);
                }
                if (nonCollectionType.isAssignableFrom(HashMap.class)) {
                    return (T) new HashMap() {{
                        putAll(result);
                    }};
                } else if (nonCollectionType.isAssignableFrom(JSONObject.class)) {
                    return (T) result;
                } else if (canInitiate(nonCollectionType)) {
                    try {
                        Map resultMap = (Map) nonCollectionType.newInstance();
                        resultMap.putAll(result);
                        return (T) resultMap;
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw castFailedException(stringData, nonCollectionType, e);
                    }
                }
            }
            if (nonCollectionType.isAssignableFrom(Date.class)) {
                return (T) DateUtil.toDate(stringData);
            }
            if (nonCollectionType.isEnum()/*Enum.class.isAssignableFrom(nonCollectionType)*/) {
                Class<Enum> enumClazz = (Class<Enum>) nonCollectionType;
                return (T) Enum.valueOf(enumClazz, stringData);
            }
            LOG.debug("以下把要转换的目标类型视作java bean来处理");
            return JSON.parseObject(stringData, nonCollectionType);
        } else if (isBasicType(nonCollectionData)) {
            if (nonCollectionType == String.class) {
                return (T) nonCollectionData.toString();
            }
            if (nonCollectionData instanceof Number/* && Number.class.isAssignableFrom(nonCollectionType)*/) {
                Number value = (Number) nonCollectionData;
                if (nonCollectionType == int.class || nonCollectionType == Integer.class) {
                    if (Integer.MAX_VALUE < value.doubleValue() || Integer.MIN_VALUE > value.doubleValue()) {
                        throw castFailedException(nonCollectionData, nonCollectionType, new Throwable("极限值越界:int"));
                    }
                    return (T) new Integer(value.intValue());
                }
                if (nonCollectionType == Long.class || nonCollectionType == long.class) {
                    if (Long.MAX_VALUE < value.doubleValue() || Long.MIN_VALUE > value.doubleValue()) {
                        throw castFailedException(nonCollectionData, nonCollectionType, new Throwable("极限值越界:long"));
                    }
                    return (T) new Long(value.longValue());
                }
                if (nonCollectionType == Short.class || nonCollectionType == short.class) {
                    if (Short.MAX_VALUE < value.doubleValue() || Short.MIN_VALUE > value.doubleValue()) {
                        throw castFailedException(nonCollectionData, nonCollectionType, new Throwable("极限值越界:short"));
                    }
                    return (T) new Short(value.shortValue());
                }
                if (nonCollectionType == float.class || nonCollectionType == Float.class) {
                    if (Float.MAX_VALUE < value.doubleValue() || Float.MIN_VALUE > value.doubleValue()) {
                        throw castFailedException(nonCollectionData, nonCollectionType, new Throwable("极限值越界:float"));
                    }
                    return (T) new Float(value.floatValue());
                }
                if (nonCollectionType == double.class || nonCollectionType == Double.class) {
                    return (T) new Double(value.doubleValue());
                }
                if (nonCollectionType.isAssignableFrom(Date.class)) {
                    if (Long.MAX_VALUE < value.doubleValue() || Long.MIN_VALUE > value.doubleValue()) {
                        throw castFailedException(nonCollectionData, nonCollectionType, new Throwable("极限值越界:long"));
                    }
                    return (T) new Date(value.longValue());
                }
            }
            throw castFailedException(nonCollectionData, nonCollectionType);
        } else if (nonCollectionData instanceof Map) {
            //只支持type=   Map /HashMap /JSONObject / 可实例化的javaBean
            if (Map.class == nonCollectionType) {
                return (T) nonCollectionData;
            }
            if (HashMap.class == nonCollectionType) {
                return (T) new HashMap() {{
                    putAll((Map) nonCollectionData);
                }};
            }
            if (JSONObject.class == nonCollectionType) {
                return (T) JSON.toJSON(nonCollectionData);
            }
            if (String.class == nonCollectionType) {
                return (T) JSON.toJSONString(nonCollectionData);
            }
            /*if (canInitiate(nonCollectionType)) {*/
            return ((JSONObject) JSON.toJSON(nonCollectionData)).toJavaObject(nonCollectionType);
            /*} else {
                throw new RuntimeException("不可实例化目标类型:" + nonCollectionType);
            }*/
        } else if (nonCollectionData instanceof Enum) {//data是枚举对象
            if (String.class == nonCollectionType) {
                return (T) ((Enum) nonCollectionData).name();
            }
        } else {//data 是 java 对象    只支持type= Map /HashMap /JSONObject / 可实例化的javaBean
            try {
                JSONObject value = (JSONObject) JSON.toJSON(nonCollectionData);
                if (nonCollectionType.isAssignableFrom(JSONObject.class)) {
                    return (T) value;
                }
                if (HashMap.class == nonCollectionType) {
                    return (T) new HashMap() {{
                        putAll(value);
                    }};
                }
                if (String.class == nonCollectionType) {
                    return (T) value.toJSONString();
                }
                return value.toJavaObject(nonCollectionType);
            } catch (Throwable unknownError) {
                throw castFailedException(nonCollectionData, nonCollectionType, unknownError);
            }
        }
        throw castFailedException(nonCollectionData, nonCollectionType);
    }

    private static RuntimeException castFailedException(Object data, Class type, Throwable... cause) {
        RuntimeException e = new RuntimeException(String.format(
                "不支持的类型转换：\r\n" +
                        "\t%s\t-->\t%s\r\n" +
                        "\tvalue=【 %s 】",
                data.getClass(), type, data));
        if (cause != null && cause.length > 0) {
            e.initCause(cause[0]);
        }
        return e;
    }

    private static final LoadingCache<Class<?>, Boolean> map = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.MINUTES)
            .maximumSize(100000)
            .removalListener((RemovalListener<Class, Boolean>) notification -> {
                LOG.info(new JSONObject() {{
                    put("type", "cacheRemoved");
                    put("data", notification);
                }});
            })
            .build(new CacheLoader<Class<?>, Boolean>() {
                public Boolean load(Class<?> type) {
                    Boolean can = null;
                    if (Modifier.isAbstract(type.getModifiers()) || type.isInterface()) {
                        can = false;
                    } else {
                        try {
                            Constructor<?> constructor = type.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            constructor.newInstance();
                            /*
                            We Suit the Fastjson deserialization. Private declared default constructor is ok here.
                            */
                        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                            can = false;
                        }
                    }
                    if (can == null) {
                        can = true;
                    }
                    return can;
                }
            });

    /**
     * 检查指定的类是否可以实例化
     */
    public static boolean canInitiate(Class type) {
        return map.getUnchecked(type);
    }

    //判断某个对象是否是java的基本类型
    public static boolean isBasicType(Object o) {
        Class type = o.getClass();
        return type == Integer.class ||
                type == Long.class ||
                type == Short.class ||
                type == Double.class ||
                type == Float.class ||
                type == Character.class ||
                type == Byte.class ||
                type == Boolean.class;
    }

    /**
     * @param getter 方法名 method name.
     * @return 对应的属性名 the mapped property name of the getter.
     */
    public static String getPropertyFromGetterName(String getter) {
        if (StringUtil.isEmpty(getter)) {
            LOG.warn("getter is empty!");
            return null;
        }
        String property;
        if (getter.startsWith("is")) {
            property = getter.substring(2);
        } else if (getter.startsWith("get")) {
            property = getter.substring(3);
        } else {
            throw new RuntimeException("bad getter name:" + getter);
        }
        String firstChar = property.charAt(0) + "";
        return property.replaceFirst(firstChar, firstChar.toLowerCase());
    }

    public static List<String> getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        List<String> parameterNames = new ArrayList<>();
        for (Parameter parameter : parameters) {
            if (!parameter.isNamePresent()) {
                throw new IllegalArgumentException("Parameter names are not present!");
            }

            String parameterName = parameter.getName();
            parameterNames.add(parameterName);
        }
        return parameterNames;
    }

    /**
     * 支持primitive类名的class.forName<br>
     * extension of {@link Class#forName(String)} with primitive class supported.
     *
     * @param className 可以传入int,char,boolean等等。 you can pass int,char,boolean etc...
     * @throws ClassNotFoundException when bad class name is passed to this method.
     */
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return ForName.forName(className);
    }

    public static final class ForName {
        private ForName() {
        }

        private static final Map<String, Class<?>> PRIM = (
                Collections.unmodifiableMap(
                        new HashMap<String, Class<?>>(16) {
                            {
                                for (Class<?> cls : new Class<?>[]{
                                        void.class,
                                        boolean.class,
                                        char.class,
                                        byte.class,
                                        short.class,
                                        int.class,
                                        long.class,
                                        float.class,
                                        double.class
                                }) {
                                    put(cls.getName(), cls);
                                }
                            }
                        }
                )
        );

        public static Class<?> forName(final String name)
                throws ClassNotFoundException {
            final Class<?> prim = PRIM.get(name);

            if (prim != null)
                return prim;

            return Class.forName(name);
        }
    }

}
