package info.xiancloud.plugin.dao.mongodb;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.StringCodec;
import org.bson.types.ObjectId;

/**
 * 自定义的string 编码解码器。
 * 使用如下代码来构造一个codeRegistry：
 * fromCodecs(new StringCodecExt())
 *
 * @deprecated 但是它解决不了insert场景自动填入objectID的hexString的问题。
 */
public class StringCodecExt extends StringCodec {

    @Override
    public String decode(BsonReader reader, DecoderContext decoderContext) {
        if (reader.getCurrentBsonType() == BsonType.OBJECT_ID) {
            return reader.readObjectId().toString();
        }
        return super.decode(reader, decoderContext);
    }

    @Override
    public void encode(BsonWriter writer, String value, EncoderContext encoderContext) {
        try {
            writer.writeObjectId(new ObjectId(value));
        } catch (IllegalArgumentException hexStringError) {
            super.encode(writer, value, encoderContext);
        }
    }
}
