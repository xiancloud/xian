### gelf客户端
#### gelf协议
概念：gelf就是一个数据格式的定义规范。  
gelf协议可以用来传输来自任何终端设备、服务器采集的数据。gelf协议定义的是数据体payload格式规范，它不局限于特定的网络传输协议，它支持数据压缩传输。
[gelf详细官方文档](http://docs.graylog.org/en/latest/pages/gelf.html)对GELF Payload有详细的定义。

##### gelf协议格式的数据可以同时在以下
- udp协议（服务端埋点推荐）
- tcp协议 
- http协议（web端和移动端推荐）

##### 丰富的gelf客户端lib库来支持埋点sdk开发和自定义扩展
- JavaScript gelf客户端库[log4js](https://github.com/pstehlik/gelf4j)、[gelf-stream](https://github.com/mhart/gelf-stream)
- ruby gelf客户端库 [gelf-rb ruby gelf library](https://github.com/graylog-labs/gelf-rb)
- [gelf-php](https://github.com/bzikarsky/gelf-php)
- Java [logstash-gelf 多种日志库支持的gelf客户端](https://github.com/mp911de/logstash-gelf)
- [logback-gelf](https://github.com/Moocar/logback-gelf)
- [graypy](https://github.com/severb/graypy) python客户端库
我们使用现成的开源稳定的gelf客户端库，而避免我们花费工作量来开发一套满足gelf协议标准的客户端代码。
- [gelfnet gelf log4net客户端库](https://github.com/jjchiw/gelf4net)

##### gelf payload示例
```json
{
  "appid": "接入方业务系统id",
  "application": "应用名称",
  "host": "日志源ip或hostname",
  "message": "消息内容",
  "version": "1.1"
}
```
##### gelf udp 演示
```bash
echo -n '{ "version": "1.1", "host": "gelf-udp.org", "short_message": "A short message transported using gelf udp protocol.", "level": 5, "_some_info": "foo" }' | nc -w0 -u alpha-log.cedarhd.com 30115
```

##### gelf tcp 演示
```bash
echo -n -e '{ "version": "1.1", "host": "gelf-tcp.org", "short_message": "A short message transported using gelf tcp protocol", "level": 5, "_some_info": "foo" }'"\0" | nc -w0 alpha-log.cedarhd.com 12201
```
##### gelf http 演示
```bash
curl -X POST -H 'Content-Type: application/json' -d '{ "version": "1.1", "host": "gelf-http.org", "short_message": "A short message transported using gelf http protocol", "level": 5, "_some_info": "foo" }' 'http://alpha-log.cedarhd.com:30158/gelf'
```
http post请求示例
```
# 请求
URL:
    https://alpha-log.abc123zxc.com/gelf
method:
    post
header:
    Content-Type: application/json
body:
    {
        "appid": "7dfee59039aee164c7fd81dd6e5c041fee51964e",
        "application": "sgcfIosApp",
        "host": "10.2.123.1",
        "message": "字符串",
        "version": "1.1"
    }

# 响应
status: 
    202 Accepted
header:
    Server →nginx/1.13.12
    Date →Wed, 10 Apr 2019 02:43:46 GMT
    Content-Length →0
    Connection →keep-alive
    Strict-Transport-Security →max-age=15724800; includeSubDomains

```

#### 采集程序
##### 服务器端日志文件采集
可以用自动化的运维脚本来实现，可以实现对服务器的使用情况的分析和监控。
##### h5和web以及APP端
基于gelf http，发送http请求给graylog服务端URL即可以将用户行为等数据发送到服务端。

##### 后端应用程序日志采集
对于Java程序，最佳实践是结合log4j1、log4j2、logback等gelf库，将日志流以udp协议发送给服务器端。
对于.NET等服务端，可以使用如gelfnet开源库来实现。
##### 等等

#### 用户行为数据结构约定
数据格式，目前是业务跟数据团队约定来做。

#### 不完美的地方
gelf自身不支持加密能力，graylog3默认没有内置解密能力，graylog3是一个开放的日志收集系统，意味着任何第三方程序只要遵循gelf协议，都可以传数据过来。未来的解决方案是给sdk增加加密能力和给graylog定制inputs插件实现解密，以及为sdk增加token认证。
