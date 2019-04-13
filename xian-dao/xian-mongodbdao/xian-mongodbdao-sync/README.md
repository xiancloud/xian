### mongodbdao-sync
这是一个依赖[MongoDB官方同步driver](http://mongodb.github.io/mongo-java-driver/3.10/driver/)的dao层ORM插件。使用该插件可以实现以ORM的方式访问
访问MongoDB。
#### 使用教程
##### 在你的业务插件中配置MongoDB插件依赖
编辑`build.gradle`文件
```gradle
dependencies {
    compile group: 'info.xiancloud', name: 'xian-mongodbdao-sync', version: "${xianVersion}"
}
```
##### 配置MongoDB数据源
在你的业务插件代码的`src/main/resources/plugin.properties`配置文件内写入配置文件：
```properties
# MongoDB的数据源完整的连接字符串，请根据实际情况调整你的参数值
mongodb_connection_string=mongodb://mongo_user:mongo_password@host:port/authenticationDatabase?maxPoolSize=100
# 你的MongoDB业务库名称，请根据实际情况调整你的参数值
mongodb_database=businessDatabaseName
```
关于MongoDB数据源连接字符串，请参考[MongoDB connection string官方文档](https://docs.mongodb.com/manual/reference/connection-string/)。

##### MongoDB基本读写操作
```java
LOG.info(" 初始化MongoDB客户端连接...");
Mongo.getOrInitDefaultDatabase(XianConfig.get("mongodb_connection_string"), XianConfig.get("mongodb_database"));
LOG.info("获取MongoDB collection对象...");
MongoCollection<GraylogMessage> collection = Mongo.getCollection("graylog_message_ext", GraylogMessage.class);
LOG.info("向MongoDB写入列表数据...");
collection.insertMany(request.getList("graylogMessages", GraylogMessage.class));

LOG.info("从MongoDB读取数据");

```

```java
/**
 * The Person Pojo.
 */
public final class Person implements Bean {
    // 注意这里id字段类型
    @JSONField(serializeUsing = ObjectIdSerializer.class, deserializeUsing = HexStringDeserializer.class)
    private ObjectId id;
    private String name;
    private int age;
    private Address address;
    ...
}
```



