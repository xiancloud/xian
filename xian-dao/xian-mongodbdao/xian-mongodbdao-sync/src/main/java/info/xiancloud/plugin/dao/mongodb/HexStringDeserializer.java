package info.xiancloud.plugin.dao.mongodb;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

/**
 * MongoDB 的objectID序列化器
 */
public class HexStringDeserializer implements ObjectDeserializer {

    @Override
    public ObjectId deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        //这里parser.getInput()获取到的是整个json字符串
        //parser.getLexer().stringVal()获取到的是指定字段的字符串值
        String hexString = parser.getLexer().stringVal();
        return new ObjectId(hexString);
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

}