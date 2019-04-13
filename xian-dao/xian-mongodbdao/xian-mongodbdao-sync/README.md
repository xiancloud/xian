## xian-mongodbdao-sync插件
这是一个依赖[MongoDB官方同步driver](http://mongodb.github.io/mongo-java-driver/3.10/driver/)的dao层ORM插件。使用该插件可以实现以ORM的方式访问
访问MongoDB。
### 使用教程
#### 在你的业务插件中配置MongoDB插件依赖
编辑`build.gradle`文件
```gradle
dependencies {
    compile group: 'info.xiancloud', name: 'xian-mongodbdao-sync', version: "${xianVersion}"
}
```
#### 配置MongoDB数据源
在你的业务插件代码的`src/main/resources/plugin.properties`配置文件内写入配置文件：
```properties
# MongoDB的数据源完整的连接字符串，请根据实际情况调整你的参数值
mongodb_connection_string=mongodb://mongo_user:mongo_password@host:port/authenticationDatabase?maxPoolSize=100
# 你的MongoDB业务库名称，请根据实际情况调整你的参数值
mongodb_database=businessDatabaseName
```
关于MongoDB数据源连接字符串，请参考[MongoDB connection string官方文档](https://docs.mongodb.com/manual/reference/connection-string/)。

#### xian-mongodbdao-sync的基本操作
伪代码示例
```java
import info.xiancloud.plugin.dao.mongodb.Mongo;

...

LOG.info(" 初始化MongoDB客户端连接...");
Mongo.getOrInitDefaultDatabase(XianConfig.get("mongodb_connection_string"), XianConfig.get("mongodb_database"));
LOG.info("获取MongoDB collection对象，如果配置文件指定的MongoDB业务库内不存在指定的集合名称，那么新建这个集合...");
MongoCollection<Person> collection = Mongo.getCollection("collectionName", Person.class);
LOG.info("向MongoDB写入列表数据...");
collection.insertMany(personList, Person.class));
```

#### MongoDB collection主键_id
MongoDB数据库内collection的主键字段默认是“_id”，默认是ObjectId类型。
##### 使用MongoDB自动生成id功能
java pojo序列化和反序列化注意事项，如下代码所示：
```java
public final class Person implements Serializable {
    // 注意这里id字段类型是MongoDB官方驱动里面的ObjectId类型，这种类型支持让MongoDB为我们自动生成id
    // 为了兼容MongoDB自动生成id的ObjectId类型，这里必须配置fastjson的序列化和反序列化器
    // 当然，如果你不需要MongoDB自动生成的id，那么大可不必使用ObjectId类型作为id字段
    @JSONField(serializeUsing = ObjectIdSerializer.class, deserializeUsing = HexStringDeserializer.class)
    private ObjectId id;
    private String name;
    private int age;
    private Address address;
    ...
    // getters and setters
}
```
以上方式生成的id在MongoDB内容如下所示  
eg.
```
_id: ObjectId("5ca1ad33a2e5ac0c9ea2db10")
```

##### 使用Java应用程序自定义生成的字符串id
```java
public final class Person implements Serializable {
    // 注意这里id字段类型是String类型，这种类型不支持让MongoDB为我们自动生成id
    // 在插入MongoDB数据库前，必须由Java程序生成一个字符串id出来
    private String id;
    private String name;
    private int age;
    private Address address;
    ...
    // getters and setters
}
```
该方式由Java生成id的，在MongoDB内存储的id类型就是字符串了  
eg.
```
_id: "26e0f8d0-5de6-11e9-aab4-ca30dc42ccd6"
```

#### 分页查询封装
eg.
```
MongoCollection<Person> collection = Mongo.getCollection("collectionName", Person.class);
Mongo.Page<Person> page = Mongo.findPageByPageNumber(collection, gt("age", 0), 1, 10);
LOG.info(page);

```
说明  
1. 所有分页操作在`info.xiancloud.plugin.dao.mongodb.Mongo`内都可以找到。
2. MongoDB的查询条件operator操作见`com.mongodb.client.model.Filters`。


#### MongoDB其他基本读写操作
使用你IDE提示功能直接检查官方的`com.mongodb.client.MongoCollection`类有哪些MongoDB操作即可。
以上示例代码可以从插件内`MongodbClientOpsDemo`中找到。


