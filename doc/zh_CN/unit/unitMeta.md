# UnitMeta
UnitMeta，我们可以把它理解成是unit最小服务单元的元数据定义对象，它指定了一些unit的高级隐藏特性。下面列出目前具有的元数据属性列表：

名称|类型|说明|默认值
-|-|-|-
description|String|unit的描述文字，目前用于API文档生成|默认为null
docApi|boolean|指定xian框架内置的API文档工具是否为该unit服务单元生成开放文档|默认true
transactional|boolean|预留、暂未启用|默认false
readonly|boolean|定义当前unit是否直接访问只读的数据源，注意 该属性只对daoUnit起作用，<br/>且在开启了读写分离配置时生效|默认false
broadcast|Broadcast|定义unit“广播”特性，关于广播特性，请关注另外一篇博客|默认null
scopes|Set<String>|指定当前unit的所属scope，这个scope目前是与API网关oauth2.0协议关联的|默认[api_all]
monitorEnabled|boolean|是否开启对当前unit的监控数据的采集，目前支持的监控数据有: unit被调用瞬时并发数|默认false
transferable|boolean|中转模式开关，开启或关闭unit请求的外部消息队列。<br/>开启状态时，发送给当前unit的请求会先进入外部mq队列排队，<br/>然后unit所在的应用会消费该队列。<br/>开启中转模式的unit是可以离线的，离线后再上线，消息不会丢失|默认false
successfulUnitResponse|UnitResponse|目前仅供API doc组件使用，用于生成API文档中unit的成功响应结果示例的|UnitResponse.createSuccess()
secure|boolean|defines whether or not to check access token in api gateway, if secure is true then check else not.| defaults to true
bodyRequired|boolean|定义API网关内指向该unit的请求透传http post body数据<br/>一个典型的应用场景就是：微信、支付宝支付结果回调，透传回调请求|默认false
dataOnly|boolean|定义unit的响应内容是否直接透传给API网关的请求方，默认是返回一个unitResponse结构的json对象回去的|默认false
version|String|unit版本，目前暂未用到|1.0

参见：[UnitMeta.java](https://github.com/xiancloud/xian/blob/master/xian-core/src/main/java/info/xiancloud/core/UnitMeta.java)

