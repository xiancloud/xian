package info.xiancloud.plugin.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author happyyangyuan
 */
public class ArrayUtil {

    /**
     * 将object数组转换为原始类型数组
     *
     * @param array eg. Integer[],Boolean[],Double[]
     * @return eg. int[],  boolean[],double[]
     */
    public static Object toPrimitiveArray(Object[] array) {
        Class primitiveType;
        if (array.length > 0) {
            LOG.debug("很可能array是用new Object[length]()构造的，这个时候array.getClass().getComponentType()返回的是Object类型，这不是我们期望的" +
                    "我们希望使用元素的实际类型，这里有一个风险点，即数组类型不一致,后面可能就会抛出类型转换异常");
            primitiveType = Reflection.getPrimitiveType(array[0].getClass());
        } else {
            primitiveType = Reflection.getPrimitiveType(array.getClass().getComponentType());
        }
        Object primitiveArray = Array.newInstance(primitiveType, array.length);
        for (int i = 0; i < array.length; i++) {
            Array.set(primitiveArray, i, array[i]);
        }
        return primitiveArray;
    }

    public static boolean isPrimitiveArrayType(Class arrayType) {
        return arrayType.isArray() && arrayType.getComponentType().isPrimitive();
    }

    /**
     * 兼容原始类型数组无法强转为Object[]的问题,
     * java内原始类型数组转化为对象数组会特别麻烦,所以本方法穷举出所有的原始类型数组进行强转
     */
    public static Object[] toObjectArray(Object array) {
        //fixme 以下写法太丑陋，其实可以简单实现
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("入参必须是数组");
        }
        if (int[].class.isInstance(array)) {
            int[] intArr = int[].class.cast(array);
            Integer[] objArr = new Integer[intArr.length];
            for (int i = 0; i < intArr.length; i++) {
                objArr[i] = intArr[i];
            }
            return objArr;
        }
        if (long[].class.isInstance(array)) {
            long[] longArr = long[].class.cast(array);
            Long[] objArr = new Long[longArr.length];
            for (int i = 0; i < longArr.length; i++) {
                objArr[i] = longArr[i];
            }
            return objArr;
        }
        if (short[].class.isInstance(array)) {
            short[] arr = short[].class.cast(array);
            Short[] objArr = new Short[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (float[].class.isInstance(array)) {
            float[] arr = float[].class.cast(array);
            Float[] objArr = new Float[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (double[].class.isInstance(array)) {
            double[] arr = double[].class.cast(array);
            Double[] objArr = new Double[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (char[].class.isInstance(array)) {
            char[] arr = char[].class.cast(array);
            Character[] objArr = new Character[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (byte[].class.isInstance(array)) {
            byte[] arr = byte[].class.cast(array);
            Byte[] objArr = new Byte[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (boolean[].class.isInstance(array)) {
            boolean[] arr = boolean[].class.cast(array);
            Boolean[] objArr = new Boolean[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        return Object[].class.cast(array);
    }

    /**
     * 取交集
     */
    public static <T> List<T> getIntersection(List<T> a, List<T> b) {
        List<T> result = new ArrayList<>();
        result.addAll(a);
        result.retainAll(b);
        return result;
    }

    /**
     * 数组转list
     */
    public static List toList(Object array) {
        List<Object> myList = new ArrayList<>();
        Collections.addAll(myList, toObjectArray(array));
        return myList;
    }

    /**
     * 数组转list
     */
    public static <T> List<T> toList(Object array, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (Object o : toObjectArray(array)) {
            list.add(Reflection.toType(o, clazz));
        }
        return list;
    }

    public static <T> T[] toArray(List list, Class<T> tClass) {
        Object arrayObject = Array.newInstance(tClass, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(arrayObject, i, list.get(i));
        }
        return (T[]) arrayObject;
    }

    /**
     * 取非交集
     *
     * @return 第一个参数list内的非交集
     */
    public static <T> List<T> getNonIntersectionInListA(List<T> a, List<T> b) {
        List<T> nonIntersectionInListA = new ArrayList<>();
        for (T t : a) {
            if (!b.contains(t)) {
                nonIntersectionInListA.add(t);
            }
        }
        return nonIntersectionInListA;
    }

    /**
     * 取非交集
     */
    public static <T> List<T> getNonIntersectionUnion(List<T> listA, List<T> listB) {
        List<T> tmp = getNonIntersectionInListA(listA, listB);
        tmp.addAll(getNonIntersectionInListA(listB, listA));
        return tmp;
    }

    /**
     * concat 2 array to a new array.
     *
     * @return the result array.
     */
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}
