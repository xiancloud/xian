package info.xiancloud.plugin.dao.mongodb;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

public class BsonObjectIdCodecDecodeToString implements Codec<String> {
    @Override
    public String decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readObjectId().toString();
    }

    @Override
    public void encode(BsonWriter writer, String value, EncoderContext encoderContext) {
        writer.writeObjectId("id",new ObjectId(value));
    }

    @Override
    public Class<String> getEncoderClass() {
        return String.class;
    }
}
