package info.xiancloud.plugin.dao.mongodb;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * MongoDB 的objectID序列化器
 */
public class ObjectIdSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object valueObject, Object fieldName, Type fieldType, int features) throws IOException {
        ObjectId value = (ObjectId) valueObject;
        String id = value.toString();
        serializer.write(id);
    }
}
