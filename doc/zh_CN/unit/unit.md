# unit详解
## unit一句话定义
unit是xian框架的最小服务单元

* [unit与API接口](https://github.com/xiancloud/xian/blob/master/doc/zh_CN/unit/unit.md#unit与API接口)
* [unit与注册中心](https://github.com/xiancloud/xian/blob/master/doc/zh_CN/unit/unit.md#unit与注册中心)
  * [unit与服务发现与API网关服务关系](https://github.com/xiancloud/xian/blob/master/doc/zh_CN/unit/unit.md#unit与服务发现与API网关服务关系)
* [unit元数据——UnitMeta](unitMeta.md)

## unit与API接口
unit作为xian微服务框架的最小服务单元，每个unit对应着一个API，每个API通过http URL暴露给接入方，实现原理如下
1. unit有一个group名称
2. unit有一个unit名称
3. API网关会解析URI请求路径得到group和unit名称然后将请求路由到指定的unit服务单元，如下图
![API网关解析URI请求路径然后将请求路由到指定的unit](http://processon.com/chart_image/5cd02ac0e4b06bcc13984bd9.png)
这样来看，你是否发现unit与MVC模式中的controller很类似？是的，unit在这里就是`分布式的controller`。
## unit与注册中心
unit作为xian微服务框架的最小服务单元，所有的unit都会被注册到注册中心内，从而实现服务发现，我们借助服务发现，便可以在其他微服务通过rpc调用到另一个微服务中指定的unit了。也可以从API网关外部通过http请求调用到指定的unit。

### unit与服务发现与API网关服务关系
![unit与服务发现与API网关服务关系](http://processon.com/chart_image/5ccfdfe0e4b0bab909739b3f.png?_=1557149767299)
