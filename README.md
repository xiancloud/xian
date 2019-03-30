# xian
xian是一个微服务框架，更确切的说是一个微服务套件。它基于Java8编写，不依赖spring，不依赖dubbo，上手和学习难度非常小。如果是以学会使用为目的，只要你会Java语言会gradle构建工具，甚至不需要了解微服务的各种概念，比学会使用dubbo和spring cloud不知道简单多少倍。

很开心地告诉大家，现在我们可以基于xian开发100%异步的微服务了！我们基于netty非阻塞io和rxJava2响应式编程风格，实现了以同步风格代码开发异步业务逻辑。
从gateway到业务层，整个业务生命周期都是异步的！也已经支持了异步的DAO层实现，仅限于postgresql。由于mysql connectorJ是基于JDBC的实现，因此对于MySQL，我们局限于JDBC阻塞的标准API。
至此，xian也是actor模型的完整实现了，希望哪天可以跟akka、vertx做性能pk！

## 目录
1. [xian的基础介绍](doc/zh_CN/xianBasis.md)  
2. [30分钟学会使用xian frame开发微服务](doc/zh_CN/quickStart.md)  
    2.1 [如何运行？程序的入口？——xian_template](doc/zh_CN/quickStart.md#如何运行？程序的入口？——xian_template)  
    2.2 [编写一个微服务单元](doc/zh_CN/quickStart.md#编写一个微服务单元)  
    2.3 [基础概念参考](doc/zh_CN/quickStart.md#基础概念参考)  
3. [xian现有功能](doc/zh_CN/xianFunctionList.md)  
4. [与其他框架对比](doc/zh_CN/comparison.md)

## 写在末尾
1. xian，即“贤”，来自某个人的名，代表“好”、“佳”的意思。谨以此名字纪念我逝去的爱情。
2. 请大家多多指点，加星加星！关注！ 那些开发中和规划中的功能就全靠你们的星星了，可怜脸。
3. 鸣谢：  
 > http://netty.io,  
 > https://github.com/ReactiveX/RxJava,  
 > https://github.com/alibaba/fastjson,  
 > https://github.com/google/guava,   
 > https://logging.apache.org/log4j,  
 > http://zookeeper.apache.org,  
 > http://curator.apache.org,  
 > https://gradle.org;
