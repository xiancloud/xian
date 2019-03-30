# dao mongodb插件实现

### MongoDB ObjectId序列化和反序列化
#### Java pojo "id"属性
pojo的"id"属性会默认与document的"_id"字段对应
#### 解决ObjectId对象序列化和反序列化的问题
使用fastjson的注解来指定序列化和反序列化方式
```Java
@JSONField(serializeUsing = ObjectIdSerializer.class, deserializeUsing = HexStringDeserializer.class)
private ObjectId id;
````
示例见`info.xiancloud.plugin.dao.mongodb.example.official.Person.java`类


 