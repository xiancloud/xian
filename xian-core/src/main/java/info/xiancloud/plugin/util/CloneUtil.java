package info.xiancloud.plugin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Collection;

/**
 * Clone util.
 *
 * @author happyyangyuan
 */
public class CloneUtil {

    /**
     * clone a java bean. (We use fastjson.)
     *
     * @param from      the bean you want to clone from. This must be a standard bean object.
     * @param beanClass the  bean type
     * @param <T>       the generic type.
     * @return the cloned object.
     */
    public static <T> T cloneBean(T from, Class<T> beanClass) {
        if (from == null || from instanceof Collection || from.getClass().isArray()) {
            throw new IllegalArgumentException("Only java bean class is allowed here.");
        }
        JSONObject jsonObject = (JSONObject) JSON.toJSON(from);
        return jsonObject.toJavaObject(beanClass);
    }

}
