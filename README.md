# xian
这是另外一个微服务框架，基于Java8编写。不依赖spring，不依赖dubbo，上手和学习难度非常小。如果是以学会使用为目的，只要你会Java语言会gradle构建工具，甚至不需要了解微服务的各种概念，比学会使用dubbo和spring cloud不知道简单多少倍。

## xian frame的基础介绍
### xian frame能解决其他主流微服务框架费力才能解决或者解决不了的问题
1. 微服务粒度自由拆分，修改配置和拆分包即可实现，几乎不用修改Java代码。
2. 微服务与数据库一对一、一对多、多对多关系轻松切换，而不用修改代码的问题。
3. 几行代码实现经典的推送异步保序场景。
4. 微服务接口编排。
5. 部署和和监控问题。
6. 帮助实现devops开发运维协作能力。

### xianframe现有功能
1. 微服务间通讯RPC、MQ。
2. 方法级粒度的服务治理、服务可视化管理。
3. 集中日志收集和可视化日志查询。
4. 分布式业务链路追踪方案，可以在上述3的日志系统内查询定位出单条业务链路上完备日志线。
5. 将Java web应用集成到微服务集群内形成业务层的“微服务”，复用框架提供的自动化集成部署和横向扩展能力，目前支持任何servlet框架集成，特别对springboot做了友好支持。
6. 微服务和数据库一对一、一对多、多对多关系的灵活支持。
7. 构建部署和持续集成插件。
8. 业务监控插件。
9. 业务线程池管理和监控。
10. 服务不下线：全微服务0停服更新。
11. 内置轻量级的持久层dao插件，支持连接池监控、慢SQL监控和防SQL注入等。
12. 轻量级api网关，具有一定的api接口编排能力。
13. api文档自动化生成的能力。
14. 基于oauth2.0的api网关安全管理和ip白名单控制能力。
15. 可快速实现开放平台能力。
16. redis缓存插件，支持多redis数据源能力。
17. 分布式消息订阅和推送功能。
18. 定时任务调度功能。
19. 集中配置管理。
20. 分布式锁。
21. 多环境管理（研发、测试、生产环境）
22. 本地非集群运行模式和本地集群运行模式，方便开发阶段调试。
23. log4j-1.x、log4j-2.x日志插件
24. 短信和邮件发送插件
25. mqtt协议客户端集成
26. 对腾讯云k8s容器服务的集成
27. 对数据库读写分离的友好支持。
28. 一致性哈希算法的封装支持。
29. 基于一致性哈希算法的异步保序功能。
31. 插件式无限扩展新功能的能力。

### 正在开发中的功能
1. 内置持久层框架对分布式事务支持
2. api网关内置反向代理的功能
3. api接口编排脚本支持热更新
4. 断路器、熔断技术

### 规划中的功能
1. 基于api网关内置反向代理实现灰度/蓝绿/红黑发布。
2. 集成rxJava实现纯异步的微服务调用模式，可完全杜绝线程阻塞情况的发生，预估可成倍提升业务线程的性能。
3. 不局限于特定语言，将来会率先支持.NET语言实现微服务，帮助解决许多传统企业历史信息系统转型互联网微服务架构。可行性方面，本框架已经抽象出了rpc通信协议规范和服务治理规范，因此几乎其他所有OOP语言都可以集成进来。
4. 基于“录音机”的API自动化测试方案。
5. 分库分表方案。
6. 对rancher管理平台的rest api集成。

### 与各大主流微服务框架的优劣对比
#### 与spring cloud对比
1. xianframe不仅仅是个微服务框架，它也是一个微服务套件，但是它做不到像springcloud那么强大能集成业界N多优秀的第三方开源lib形成丰富的套装，目前Spring Cloud下面有17个子项目（可能还会新增）分别覆盖了微服务架构下的方方面面，我这个轻量级的微服务框架怎可与之庞然大物比呢。
2. 以上是springcloud的优势，这个优势的背后是它引入了无数第三方依赖包，一个基础springcloud微服务就已经引入了接近100个第三方依赖的jar包，这么第三方依赖往往很容易赖冲突，比如你的程序要使用log4j，而它默认使用的是logback，你会发现二者是冲突的，然后你还得想办法兼容起来，耗时也费力。
3. xianframe的拆分灵活性秒杀springcloud，xianframe是基于方法级粒度的服务治理框架，而springcloud是基于微服务application粒度的服务治理。xianframe可以在不修改任何代码的情况下，实现线上微服务服务平滑拆分。而对于springcloud，你会发现随着你方系统的复杂度越来越高，将来必定会有拆分微服务的需求，这时你会发现，你需要修改每个rpc调用处的注解上的目标app名称，而且还要剪切粘贴很多地方的代码，这是一个很大的工作量，改动量大，回归测试难度大，风险高。
3. xianframe的rpc方案是基于netty Nio框架实现，使用纯异步IO非阻塞线程，以socket长连接形式实现rpc调用和通讯的，因此理论上性能是远大于springcloud的微服务间基于七层网络http短连接通信协议的。由于我还没有时间进行测试，所以这里没法给出具性能对比数据出来。
4. 当然xianframe和springcloud一样都还没支持多语言，即都还没满足微服务的一个关键特性，即不依赖特定语言来开发微服务业务。不过让xianframe支持.NET、golang、kotlin等语言，我已经有一些想法了。
5. 题外话，打个小广告，如果你对springcloud感兴趣，同时你习惯使用gradle构建工具的，推荐一个我写的基于gradle的springcloud入门教程https://github.com/happyyangyuan/springcloud-quickstart

#### 与dubbo的对比
1. dubbo服务提供方与调用方接口依赖方式太强：调用方对提供方的抽象接口存在强依赖关系，需要严格的管理版本依赖，才不会出现服务方与调用方的不一致导致应用无法编译成功等一系列问题；而xianframe调用方与被调用方之间是物理强解耦的，没有接口依赖关系，只有逻辑依赖关系，只要参数和响应是适配的，就能相互兼容，尤其是单方面增加和减少可选字段参数完全不影响对端的兼容性。
2. 外部系统无法直接对接dubbo协议，即服务对平台敏感，难以简单复用：通常我们在提供对外服务时，都会以REST的方式提供出去，这样可以实现跨平台的特点。在dubbo中我们要提供REST接口时，不得不实现一层代理，用来将RPC接口转换成REST接口进行对外发布。所以当当网在dubbox（基于dubbo的开源扩展）中增加了对REST支持。而xianframe已经通过http gateway网关将xian内部的服务一一映射成为了可以直接访问的httpURI地址了，外部系统可以访问当xianframe提供的任何微服务而不需要任何二次开发来实现一个http代理server来提供rest服务。
3. dubbo只是一个rpc框架，而xianframe是一个包含服务治理、调用链路追踪、dao层内置框架、集中配置、分布式锁、缓存技术、内置监控、部署和持续集成解决方案等等的微服务框架套件。
4. 当然xianframe和springcloud一样都还没支持多语言，即都还没满足微服务的一个关键特性，即不依赖特定语言来开发微服务业务。不过让xianframe支持.NET、golang、kotlin等语言，我已经有一些想法了。


#### 与华为开源的combbo对比
暂时还没写

#### 与国内基于jfinal的jboot的对比
暂时还没写

## 30分钟学会使用xian frame开发微服务

### 引入依赖
我在GitHub上给出了一个gradle项目模板，详见https://github.com/happyyangyuan/xian_template 。

#### /xian_template/gradle.properties
配置文件指明了xian依赖的版本号
````
xianVersion=0.1.0-beta
````
#### /xian_template项目内的子module：demo_plugin01、demo_plugin02、demo_web_plugin01
demo_plugin01定义了unit类 /xian_template/demo_plugin01/src/main/java/com/yourcompany/demoplugin01/unit/DemoUnit01.java
该unit类调用另外一个unit类：/xian_template/demo_plugin01/src/main/java/com/yourcompany/demoplugin02/unit/DemoUnit02.java
二者形成rpc调用关系，具体见这两个unit代码的execute方法体。
demo_web_plugin01定义了一个Java web应用，里面内置了一个hello world的index.html页面。

#### 可运行的application
我们在/xian_template/xian_runtime/下存在四个application如下：demoApplication01、demoApplication02、demoGateway、demoWebApplication01。
我们将每个application看作是一个微服务，下面依次讲解。

1. demoWebApplication01，我们将demo_web_plugin01部署在这个application内了，因此它是一个web应用，部署配置见xian_runtime/demoWebApplication01/build.gradle。demoWebApplication01以“微服务”的身份定义于微服务集群内。我们可以在任意位置运行脚本 ./demoWebApplication01/build.sh执行gradle构建，然后执行运行程序：./xian_runtime/demoWebApplication01/_start.sh。启动后访问http://localhost:8080 查看效果。我们可以运行./xian_runtime/demoWebApplication01/stop.sh来停止该服务。

2. demoGateway，这是我们xianframe关键的业务网关application。它内置了一个高性能netty httpserver作为网关server对外提供服务，默认端口是9124，并且可配置。

3. demoApplication01、demoApplication02分别部署了demo_plugin01和demo_plugin02，两个application之间形成了rpc调用关系。我们执行./xian_runtime/buildAll.sh构建所有application, 然后运行各自application内的_start.sh脚本可以启动他们。

4. 访问curl -XPOST http://localhost:9124/demoGroup01/demoUnit01 查看对DemoUnit01的访问效果，同样的你可以访问 curl -XPOST http://localhost:9124/demoGroup02/demoUnit02 来访问DemoUnit02，不过它会提示缺少参数，需要什么参数可以参见DemoUnit02的实现。

#### 惯例和约定
1. 从上文你不难看出，每一个unit都以http api形式通过demoGateway暴露给外部了，这个URI的格式如上所述： http://gatewayHost:gatewayPort/groupName/unitName
http method为post，这是xianframe的网关标准。
2. 而demoUnit01调用demoUnit02的rpc标准代码如下：
````
Xian.call("demoGroup02", "demoUnit02",
                new JSONObject().fluentPut("param", msg.get("param", "a temp param if not absent.")));
//详见DemoUnit01.java类
//上面'demoGroup02'为目标Unit的groupName，'demoUnit02'为目标unit的名称。
````

以上是同步调用，很多时候，我们希望异步方式实现任务提交，示例如下：
````
Xian.call("demoGroup01", "demoUnit01",new JSONObject(), new NotifyHandler(){
 handle(UnitResponse response){
    //doSth with the response.
 }
});
````

3. 我们为大家准备的project template是方便大家基于此template来扩展新的微服务，而不用浪费时间来自己开发gradle和shell脚本了，请遵循以下xian_template规范如下
 a. 所有的application必须定义在xian_runtime/内，所有的application都是由plugin组装而成的，plugin列表配置在/xian_runtime/applicationName/build.gradle的依赖列表内。
 b. application的名称就是xian_runtime/子路径名。
 c. application的启动和停止脚本已经内置，请直接使用即可。
 d. 更新程序后，需要执行build.sh/buildAll.sh重新构建。
 e. 构建后，xian_runtime里面的所有的application包都是一个可运行的包，你可以将xian_runtime整个拷贝至服务器上并重命名为xian_runtime_test，然后运行各个application的启动脚本start.sh。如果需要将application运行多个实例，可以复制多份。需要注意的是，我们使用路径中的xian_runtime_env来标识集群环境，比如xian_runtime_test/标识其内运行的application为test集群环境，xian_runtime_production/标识其内运行的application为production环境。
 
4. 以上使用启动脚本来运行各个节点的方式我们成为集群模式

5. xianframe的IDE内非集群模式
子module /xian_template/test内可以开发Junit代码或者直接写main入口代码进行单元测试，它将所有的本project定义的unit统一在本地管理，而不使用注册中心，我们可以直接使用rpc工具类"Xian.java"来本地调用的各个unit。详见/xian_template project内的DemoUnitTest.java类。


#### demo集群模式application关系图如下
暂时略



### 基础概念参考
#### 服务单元unit
unit是本微服务框架的基础服务单元最小粒度，每个unit对应着一个Java方法，我们会将每个unit注册到注册中心。
#### 单元组group
group定义了unit分组，每个unit都属于唯一一个group，groupName和unitName二者唯一确定一个unit定义。
#### 插件plugin
插件是多个特定的unit的组合而成的一个项目子module，我们将插件自由组合而成为application。

#### 其他待补充




## 写在末尾
1. xian的命名来自某个人的名，代表“好”、“佳”的意思。谨以此名字纪念我死去的爱情。
2. 请大家多多指点，加星加星！关注！ 那些开发中和规划中的功能就全靠你们的星星了，可怜脸。


