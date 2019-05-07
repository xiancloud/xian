# 集中日志管理
xian框架内置了集中日志收集和可视化日志查询，提供了gelf4j日志插件和graylog集成。

## 工作原理
### gelf协议
[gelf协议的详细描述见这一份文档](gelf.md)

### 性能考虑
- 采用gelf支持的udp协议传输网络日志，由于udp协议是纯净的报文传输协议，日志服务以及网络带宽因素都不会对Java程序造成性能影响。
- 完全基于网络日志，而不写本地硬盘，避免硬盘io资源消耗，只消耗本地网络带宽和部分网卡数据传输性能。

### log4j2
我们推荐在xian中使用log4j2.x来作为底层日志框架，这也是目前xian_template采用的默认的日志框架。

### xian-gelf-common插件
该插件集成了log4j2的gelf客户端，使用udp协议将log4j日志直接发送给远程graylog服务器，而不写本地日志文件，避免对硬盘io的消耗。<br/>
xian_template中引入该插件的gradle配置文件路径：
```
/xian_template
├──xian_runtime/
│   ├── apidocApplication/
│   ├── demoApplication01/
│   └── demoGateway/
└── build.gradle # 所有的微服务application默认的依赖定义在这里
```
配置内容：
```gradle
subprojects {
    dependencies {
      // 这里引入了xian-log4j2插件依赖，它本质上就是一个log4j2.x的插件封装。
      runtime group: 'info.xiancloud', name: 'xian-log4j2', version: "$xianVersion"
      // 这里引入了xian-gelf-common插件依赖，它本质就是gelf appender实现
      runtime group: 'info.xiancloud', name: 'xian-gelf-common', version: "$xianVersion"
      // ... 
      // 此处省略其他引入的依赖
    }
```

#### log4j gelf appender
相信大家对log4j的appender不会陌生，它是用来将日志分流输出到不同的目标上去的，比如
- 标准输出`ConsoleAppender`
- 文件输出`FileAppender`
- `GelfLogAppender`——我这里的自定义输出
##### GelfLogAppender详解
该log4j2.x appender原理很简单，就是按照log4j2.x的接入规范实现的一个GelfLogAppender，让log4j的日志输出到指定的网络地址上。
详见源码[GelfLogAppender.java](https://github.com/xiancloud/xian/blob/master/xian-log/xian-gelf-common/src/main/java/biz/paluch/logging/gelf/log4j2/GelfLogAppender.java)

## 日志开发指南
### graylog udp server地址配置
- 使用application.properties来配置
- 使用环境变量来配置
- 环境变量优先级高于application.properties（这更符合容器化方案的风格）
#### 使用application.properties来配置
配置文件路径：/xian_template/xian_runtime/${application}/conf/application.properties
```
/xian_template
├──xian_runtime/
│  ├───apidocApplication/
│  │   └──conf/
│  │     └──application.properties # gelf udp server配置，请关注此配置文件
│  ├───demoApplication01/
│  └───demoGateway/
```
/xian_template/xian_runtime/${application}/conf/application.properties配置示例：
```properties
#gelf client plugin
gelfInputPort=${graylog_port}
gelfInputInternetUrl=udp:${graylog_hostname}
```
ps：请根据自己的graylog地址修改上述配置内容中的变量

#### 也可以使用环境变量指定graylog server地址和udp端口
```bash
# 可以在服务器上面执行如下命令来配置环境变量
export gelfInputPort=${graylog_port}
export gelfInputInternetUrl=udp:${graylog_hostname}
```
ps：请根据自己的graylog地址修改上述配置内容中的变量

### 代码示例
```
//打印 info 日志，graylog上面对应的info级别日志是`level:6`
LOG.info("info级别业务日志内容。");

//打印warning级别日志，graylog上面对应的warn级别日志是`level:4`
LOG.warn("warn级别业务日志内容。");
LOG.warn("warn级别业务日志内容。", exception);

//打印error级别的日志，graylog上面对应的error级别的日志是`level:3`
LOG.error("error级别业务日志内容。");
LOG.error("error级别业务日志内容。", exception);
```

## 附录
### graylog
graylog是一个优秀的开源日志管理系统
- graylog提供了优雅的图形界面来展示日志
- graylog提供了ldap、active directory接入认证
- graylog提供了基于RBAC（role based access control）权限管理能力
有些graylog追随者这样评价它：
> graylog简直就是开源版本的[splunk](https://www.splunk.com/)
一张图来描述graylog
![一张图来描述graylog](http://processon.com/chart_image/5c6e5414e4b056ae2a115c30.png)

关于graylog更丰富的描述，请移步[graylog官网](https://www.graylog.org/)。
