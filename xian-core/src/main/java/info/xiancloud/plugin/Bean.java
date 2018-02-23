package info.xiancloud.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.util.Reflection;

import java.io.Serializable;
import java.util.Map;

/**
 * Input Bean接口
 *
 * @author hang、yyang 建议这个bean不仅支持input参数也支持出参，即把他作为一个bean的超类来使用
 */
public class Bean implements Serializable {

    /**
     * Convert this bean into Map(String,Object)  <br>
     * The same as {@link #toJson()}
     */
    public Map<String, Object> toMap() {
        return toJson();
    }

    /**
     * cast this bean into json object.
     */
    public JSONObject toJson() {
        return Reflection.toType(this, JSONObject.class);
    }

    /**
     * cast this bean into specified Type
     */
    public <T> T toType(Class<T> clazz) {
        return Reflection.toType(this, clazz);
    }

    public static <T extends Bean> T jsonToBean(JSONObject json, Class<T> clazz) {
        return json.toJavaObject(clazz);
    }

    public static <T extends Bean> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        return ((JSONObject) JSON.toJSON(map)).toJavaObject(clazz);
    }
}
