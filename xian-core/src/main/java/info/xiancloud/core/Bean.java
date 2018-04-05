package info.xiancloud.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.Reflection;

import java.io.Serializable;
import java.util.Map;

/**
 * It is suggested that all java beans should implement this super bean interface.
 *
 * @author happyyangyuan
 */
public interface Bean extends Serializable {

    /**
     * Convert this bean into Map(String,Object)  <br>
     * The same as {@link #toJson()}
     */
    default Map<String, Object> toMap() {
        return toJson();
    }

    /**
     * cast this bean into json object.
     */
    default JSONObject toJson() {
        return Reflection.toType(this, JSONObject.class);
    }

    /**
     * cast this bean into specified Type
     */
    default <T> T toType(Class<T> clazz) {
        return Reflection.toType(this, clazz);
    }

    static <T extends Bean> T jsonToBean(JSONObject json, Class<T> clazz) {
        return json.toJavaObject(clazz);
    }

    static <T extends Bean> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        return ((JSONObject) JSON.toJSON(map)).toJavaObject(clazz);
    }

}
