package info.xiancloud.plugin.dao.mongodb.example.unit;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import info.xiancloud.core.*;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.plugin.dao.mongodb.Mongo;
import info.xiancloud.plugin.dao.mongodb.example.official.Address;
import info.xiancloud.plugin.dao.mongodb.example.official.Person;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class MongodbClientOpsDemo implements Unit {

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDocApi(false).setSecure(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Group getGroup() {
        return MongodbGroupDemo.SINGLETON;
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
        handler.handle(insert());
    }

    private static UnitResponse insert() {
        MongoCollection<Person> collection = Mongo.getCollection("dianping-collection", Person.class);
        // make a document and insert it
        Person person = new Person("张三", 20, new Address("南村万博", "广州", "10086"));
        System.out.println("Original Person Model: " + person);
        collection.insertOne(person);
        // Person will now have an ObjectId
        System.out.println("Mutated Person Model: " + person);
        return UnitResponse.createSuccess(person);
    }

    private static void update() {
        MongoCollection<Person> collection = Mongo.getCollection("dianping-collection", Person.class);
        //更新一条document
        collection.updateOne(eq("name", "张三"), combine(set("age", 23), set("name", "Ada Lovelace")));

        // 更新多条document
        UpdateResult updateResult = collection.updateMany(not(eq("zip", null)), set("zip", null));
        System.out.println(updateResult.getModifiedCount());

        // 替换collection（理论上object id是不会被替换的）
        updateResult = collection.replaceOne(eq("name", "张三"), new Person("张三", 20, new Address("香柏广场", "广州", "10086")));
        System.out.println(updateResult.getModifiedCount());
    }


    private static void find() {
        MongoDatabase database = Mongo.getOrInitDefaultDatabase();
        MongoCollection<Person> collection = database.getCollection("dianping-collection", Person.class);
        // get it (since it's the only one in there since we dropped the rest earlier on)
        Person somebody = collection.find().first();
        System.out.println(somebody);

        System.out.println("");
        // now lets find every over 30
        for (Person person : collection.find(gt("age", 0))) {
            System.out.println(person);
        }
    }

    // 分页
    private static void findPage() {
        MongoCollection<Person> collection = Mongo.getCollection("dianping-collection", Person.class);
        Mongo.Page<Person> page = Mongo.findPageByPageNumber(collection, gt("age", 0), 1, 10);
        System.out.println(page);
    }

    public static void main(String[] args) {
//        findPage();
        SingleRxXian.call(MongodbClientOpsDemo.class).blockingGet();
//        insert();
//        find();
    }

}
