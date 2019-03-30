package info.xiancloud.plugin.dao.mongodb;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.plugin.dao.mongodb.example.official.Person;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

public class ObjectIdSerializationTest {
    private static String hexString = "5c84b00a5ae358185a6d0cf1";

    @Test
    public void deserialize() {
        System.out.println("反序列化器单元测试");
        Person person = Reflection.toType(
                new JSONObject().fluentPut("id", hexString).toJSONString(),
                Person.class);
        Assert.assertEquals(person.getId().toHexString(), hexString);
    }

    @Test
    public void serialize() {
        System.out.println("序列化单元测试");
        Person person = new Person();
        person.setId(new ObjectId(hexString));
        System.out.println(JSON.toJSONString(person));
        System.out.println(Reflection.toType(person, String.class));
        Assert.assertTrue(Reflection.toType(person, String.class).contains(hexString));
    }

    public static void main(String[] args) {
        Person person = new Person();
        person.setId(new ObjectId(hexString));
        JSON.toJSONString(person);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(person);
        System.out.println(UnitResponse.createSuccess(person).toVoJSONString(true));
        System.out.println(jsonObject.toJSONString());
        System.out.println(JSON.toJSONString(person));
    }

}
