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
该插件集成了log4j2的gelf客户端，使用udp协议将log4j日志直接发送给远程graylog服务器，而不写本地日志文件，避免对硬盘io的消耗。
#### log4j gelf appender
相信大家对log4j的appender不会陌生，它是用来将日志分流输出到不同的目标上去的，比如
- 标准输出`ConsoleAppender`
- 文件输出`FileAppender`
- `GelfLogAppender`——我这里的自定义输出
##### GelfLogAppender详解
该log4j2.x appender原理很简单，就是按照log4j2.x的接入规范实现的一个GelfLogAppender，让log4j的日志输出到指定的网络地址上。
详见源码[GelfLogAppender.java](https://github.com/xiancloud/xian/blob/master/xian-log/xian-gelf-common/src/main/java/biz/paluch/logging/gelf/log4j2/GelfLogAppender.java)

## 日志开发指南
graylog udp server地址
代码示例
```
//打印 info 日志，graylog上面通过
LOG.info("业务日志内容。")
LOG.error("")
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
