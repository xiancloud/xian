## 30分钟学会使用xian frame开发微服务

### 如何运行？程序的入口？——xian_template
我在GitHub上给出了一个gradle项目模板，该模板已经帮你配置好了各种对xian frame的依赖。

#### 1、下载gradle项目模板源码
````bash
git clone https://github.com/happyyangyuan/xian_template
````
或者直接使用你的IDE，如IntelliJ IDEA内执行"Checkout from Version Control"来下载和导入本gradle项目模板工程。

#### 2、将以上克隆好的项目，以gradle项目方式导入到你的IDE内
其实，导入git项目到IDE有一万种方式，这里省略不描述了，熟练使用IDE是你必备技能哦。

#### 3、xian_template项目结构介绍
##### 3.1、gradle.properties配置文件
该配置文件指明了xian依赖的版本号
````properties
xianVersion=x.x.x
````

##### 3.2、 根路径内的build.gradle依赖配置文件
该文件指明了对xian-core的依赖
````gradle
compile group: 'info.xiancloud', name: 'xian-core', version: "${xianVersion}"
````
##### 3.3、 xian_runtime内定义了4个微服务application
调用关系如下图所示：
![xian_template rpc调用关系图](http://processon.com/chart_image/5a9996e7e4b0701a028658d7.png?_=1551343650234)

其中，demoGateway是网关微服务，内置了高性能netty http server。
其他微服务application接下来介绍。

##### 3.4、子module：demo_plugin01、demo_plugin02、demo_web_plugin01
子module，我们称之为“插件”。
我们将插件部署在微服务内，从而让微服务application具有业务功能。部署配置见xian_runtime/demoApplication01/build.gradle
````gradle
dependencies {
    runtime project(':demo_plugin01')
}
````
demoApplication02、demoWebApplication亦是如此。

###### 3.4.1 关注demo_plugin01内定义的“服务单元” DemoUnit01.java
该“服务单元”调用另外一个“服务单元” DemoUnit02.java，形成rpc调用关系，具体见这两个unit代码的execute方法体：
````java
public class DemoUnit01 implements Unit {
    //...
    
    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        SingleRxXian
            .call(
                    "demoGroup02",
                    "demoUnit02",
                    new JSONObject().fluentPut("param", msg.get("param", "a temp param if absent."))
                )
            .subscribe(handler::handle);
    }
}
````
rpc调用关系见上文微服务关系图。


#### 可运行的application
我们在/xian_template/xian_runtime/下存在几个application：demoApplication01、demoApplication02、demoGateway、demoWebApplication01。
我们将每个application看作是一个微服务，下面依次讲解。

1. demoWebApplication01  
插件demo_web_plugin01被部署在这个application内了，因此它是一个web应用，部署配置见xian_runtime/demoWebApplication01/build.gradle:
````gradle
runtime "info.xiancloud:xian-jettyweb:${xianVersion}"
runtime project(path: ':demo_web_plugin01', configuration: "war")
````
demoWebApplication01以“微服务”的身份定义于微服务集群内。我们可以在任意位置运行build.sh脚本来构建该application：
````./demoWebApplication01/build.sh````

构建完毕后，便可以执行启动脚本来运行程序：
````bash
./xian_runtime/demoWebApplication01/_start.sh
````

启动后访问 http://localhost:8080 查看效果。我们可以运行stop.sh脚本来停止该服务：
````bash
./xian_runtime/demoWebApplication01/stop.sh
````


2. demoGateway
这是我们xian frame关键的业务网关application。它内置了一个高性能netty httpserver作为网关server对外提供服务，默认端口是9123，并且可配置，配置文件在xian_runtime/demoGateway/conf/application.properties：
````properties
# ...
# gateway http server port, the default port is 9123 if you leave this empty.
api_gateway_port=
````

3. demoApplication01、demoApplication02  
分别部署了demo_plugin01和demo_plugin02，两个application之间形成了rpc调用关系。我们执行./xian_runtime/buildAll.sh构建所有application：
````bash
./xian_runtime/buildAll.sh
````
然后运行各自application内的_start.sh脚本可以启动他们。

4. 访问如下URL来查看对DemoUnit01的访问效果：
````bash
curl -XPOST http://localhost:9123/demoGroup01/demoUnit01
````
同样的你可以访问 curl -XPOST http://localhost:9123/demoGroup02/demoUnit02 来访问DemoUnit02，不过它会提示缺少参数，需要什么参数可以参见DemoUnit02的实现。

#### API文档微服务 apidocApplication
顾名思义，它就是为你自动生成API文档的，执行start.sh来启动该微服务，然后访问如下地址查看文档效果:  
1. [自定义的api描述文档](http://localhost:9123/apidoc/customizedHtml?docName=可定制的文档标题&docDescription=可定制的文档描述超文本&unitFilter=demoGroup01.demoUnit01,demoGroup02.demoUnit02)  
2. [全api描述文档](http://localhost:9123/apidoc/fullHtml?docName=可定制的文档标题)  
3. [单元组的api描述文档](http://localhost:9123/apidoc/groupHtml?groupName=apidoc&docName=可定制的文档标题&docDescription=可定制的文档描述超文本)  
  
tips: 可以设置你自己想要的参数来定制不同的API文档出来哦。


#### 惯例和约定
1. 从上文你不难看出，每一个unit都以http api形式通过demoGateway暴露给外部了，这个URI的格式如上所述： http://gatewayHost:gatewayPort/groupName/unitName
http method为post，这是xian frame的网关标准。
2. 而demoUnit01调用demoUnit02的rpc标准代码如下：
````java
UnitResponse unitResponse = SingleRxXian.call("demoGroup02", "demoUnit02", map/bean).blockingGet();
//详见DemoUnit01.java类
//上面'demoGroup02'为目标Unit的groupName，'demoUnit02'为目标unit的名称。
````

以上是同步调用，很多时候，我们希望异步方式实现任务提交，示例如下：
````java
SingleRxXian.call("demoGroup01", "demoUnit01",new JSONObject()).subscribe();
````

3. 我们为大家准备的project template是方便大家基于此template来扩展新的微服务，而不用浪费时间来自己开发gradle和shell脚本了，请遵循以下xian_template规范如下
* 所有的application必须定义在xian_runtime/内，所有的application都是由plugin组装而成的，plugin列表配置在/xian_runtime/applicationName/build.gradle的依赖列表内。
* application的名称就是xian_runtime/子路径名。
* application的启动和停止脚本已经内置，请直接使用即可。
* 更新程序后，需要执行build.sh/buildAll.sh重新构建。
* 构建后，xian_runtime里面的所有的application包都是一个可运行的包，你可以将xian_runtime整个拷贝至服务器上并重命名为xian_runtime_test，然后运行各个application的启动脚本start.sh。如果需要将application运行多个实例，可以复制多份。需要注意的是，我们使用路径中的xian_runtime_env来标识集群环境，比如xian_runtime_test/标识其内运行的application为test集群环境，xian_runtime_production/标识其内运行的application为production环境。
 
4. 以上使用启动脚本来运行各个节点的方式我们成为集群模式

5. xianframe的IDE运行模式

子module /xian_template/test内可以开发Junit代码或者直接写main入口代码进行单元测试，它将所有的本project定义的unit统一在本地管理，而不使用注册中心，我们可以直接使用rpc工具类"Xian.java"来本地调用的各个unit。详见/xian_template project内的DemoUnitTest.java类。


### 编写一个微服务单元
编写一个微服务单元只需要实现接口Unit即可：
````java
public class HelloWorldUnit implements Unit {
    @Override
    public String getName() { // 指定一个unit名称
        return "helloWorld";
    }
    
    @Override
    public Group getGroup() { // 指定当前微服务单元所属的group对象
        return TestGroup.singleton;
    }

    @Override
    public Input getInput() { // 指定微服务单元的入参定义
        return Input.create().add("yourName", String.class, "你的名字", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg,Handler<UnitResponse> handler) { // 当前微服务单元的执行逻辑
        UnitResponse unitResponse = UnitResponse.createSuccess("hello world, "+ msg.getString("yourName"));
        handler.handle(unitResponse); // callback回调 以返回unit执行结果
    }
}
````
定义一个微服务单元是不是很简单？ 接下来我们展示如何使用RPC来调用该服务单元：
````java
UnitResponse resp = SingleRxXian.call("test", "helloWorld", map/bean).blockingGet();//这种阻塞业务的方式，我们是非常不推荐的！这里仅仅做展示。
````
以上是同步RPC调用，下面展示异步RPC调用：
````java
SingleRxXian
    .call("test", "helloWorld", map/bean)
    .subscribe(unitResponse -> {
        // 这里可以对unitResponse进行处理
    });
    
// 链式调用， 这里如果大家对rxJava有一定的了解的话，那么以下代码你肯定信手拈来
SingleRxXian
    .call("test", "helloWorld", map/bean)
    .flatMap(unitResponse -> {
       return SingleRxXian.call("anotherGroup", "anotherUnit0", unitResponse.dataToMap());
    })
    .flatMap(unitResponse -> {
       return SingleRxXian.call("anotherGroup", "anotherUnit1", unitResponse.dataToMap());
    })
    .subscribe(unitResponse -> {
        // 这里可以对unitResponse进行处理
    });
    
````

接下来，你只需要在各个微服务内编写各自的微服务单元，然后就可以实现自己的分布式应用啦，就是这么简单！


### 基础概念参考
#### 服务单元unit
unit是本微服务框架的基础服务单元最小粒度，每个unit对应着一个Java方法，我们会将每个unit注册到注册中心。
#### 单元组group
group定义了unit分组，每个unit都属于唯一一个group，groupName和unitName二者唯一确定一个unit定义。
#### 插件plugin
插件是多个特定的unit的组合而成的一个项目子module，我们将插件自由组合而成为application。

#### 注册中心
##### zookeeper
Apache ZooKeeper是Apache软件基金会的一个软件项目，他为大型分布式计算提供开源的分布式配置服务、同步服务和命名注册。 ZooKeeper曾经是Hadoop的一个子项目，但现在是一个独立的顶级项目。 ZooKeeper的架构通过冗余服务实现高可用性。 参考自“维基百科”。

在xian_template示例项目中，我提供了一个zookeeper服务用于测试使用，请勿用于生产等环境。可以在你的application.properties中配置你zookeeper服务端地址
````properties
#service registration center internet url
zookeeperConnectionStringInternet=zk.xiancloud.info:32761
````
##### zkui
zkui是一个zookeeper的UI客户端，github开源地址：https://github.com/DeemOpen/zkui

xian_template提供了一个zkui服务：https://zkui.xiancloud.info

账号密码是：appconfig/appconfig

##### 我们使用zkui来实现集中配置管理
登录zkui后，请访问/xian_runtime_yourEnvironment/resources/来看查看和修改你的插件配置。
修改的配置会自动广播至相关节点内，因此是实时生效的。
