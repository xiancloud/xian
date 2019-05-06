# 服务发现
xian目前是基于zookeeper实现的注册中心，客户端使用的开源的[阿帕奇curator](https://curator.apache.org)
同时，我们为zk配置了一个zkui界面客户端。

关于zookeeper和zkui的概述，在[上文快速入门教程#注册中心](https://github.com/xiancloud/xian/blob/master/doc/zh_CN/quickStart.md#注册中心)中有提及。

## 服务发现原理
1. xian的微服务应用启动时，预加载apache-curator客户端与zookeeper-server建立起socket长连接。
2. Apache-curator客户端内置的zookeeper-watcher监听并同步已经在zookeeper中注册过的服务列表到本地缓存中。
3. 在其他基础组件加载ready完毕后，开始注册微服务node、group、unit到zookeeper服务器。
4. 微服务集群内其他节点内的zookeeper-watcher监听到并更新新加入到注册中心的服务单元列表。

简化版本的服务发现与注册流程如下：
![简化版本的服务发现与注册流程图](http://processon.com/chart_image/5cd06c53e4b01941c8c89211.png?_=1557164987554)
