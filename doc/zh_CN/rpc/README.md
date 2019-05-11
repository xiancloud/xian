# RPC
RPC即`Remote Procedure Call`缩写，译为远程过程调用，在这里即微服务各个节点之间的远程微服务单元的调用过程。

## 原理
我们使用了netty作为RPC的服务端和客户端通信网络框架，见gradle依赖配置文件：
```gradle
compile group: 'io.netty', name: 'netty-all', version: '4.1.19.Final'
```
### RPC需要与服务注册发现配合
![RPC与服务发现配合](http://processon.com/chart_image/5cd06c53e4b01941c8c89211.png?_=1557172429050)  
  
关于服务发现更详细的描述，见[服务发现详解](../service-discovery/README.md)

### RpcServer与RpcClient抽象
我们在`xian-core`内抽象了`RpcServer.java`和`RpcClient.java`两个接口；  
而`xian-rpcNettyClient`和`xian-rpcNettyServer`两个插件是目前我们内置提供的基于netty的rpc插件。

### rpc使用socket长连接通信
- 长连接的高性能
  1. 长连接懒加载机制
  2. 长连接复用机制
  3. 长连接回收机制
  4. 长连接平滑关闭机制
- 短连接频繁握手的性能损耗

#### 长连接懒加载机制
rpc server端会伴随着节点启动而启动，开启**随机端口**监听，见[Node.java源码](https://github.com/xiancloud/xian/blob/master/xian-core/src/main/java/info/xiancloud/core/distribution/Node.java)
```Node.java
    public static int RPC_PORT;
    
    static {
        // here we use the java ServerSocket to hele us get the random port.
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            RPC_PORT = socket.getLocalPort();
        } catch (Throwable e) {
            // ...
        }
    }
```
rpc client不会伴随着节点启动而启动与其他节点的socket长连接，而是等到当前节点需要调用另外一个节点内的unit服务单元时，才会开始建立与另外一个节点内置的netty server之间的长连接。
见源码`public final class RpcNettyClient implements RpcClient`，伪代码如下：
```RpcNettyClient.java
    public boolean request(String nodeId, String message) {
        if (!channelAvailable(nodeId)) {
            lazyInit(nodeId);
        }
        ...
        nodeId_to_connectedChannel_map.get(nodeId).writeAndFlush(message + Constant.RPC_DELIMITER);
        ...
    }
```

#### 长连接复用机制
同上文的长连接懒加载示例，是同一份代码，这里抠出来强调一下：
```java
if (!channelAvailable(nodeId)) {
   lazyInit(nodeId);
}
nodeId_to_connectedChannel_map.get(nodeId).writeAndFlush(message + Constant.RPC_DELIMITER);

## ps：lazyInit(nodeId)的逻辑是：创建一个socket长连接，将长连接的channel引用存储concurrentHashMap，以便后面对其进行复用。
```

#### 长连接回收机制
我们为了最大限度的降低程序空跑时占用的资源，这里RPC长连接还做了资源释放机制，由netty的IdleStateHandler提供技术实现：
```java
public class RpcServerIdleStateHandler extends IdleStateHandler {
    /**
     * 保证在业务超时之后再进行rpc长连接回收；20秒+30分钟
     */
    static final long IDLE_TIMEOUT_IN_MILLI = Constant.UNIT_DEFAULT_TIME_OUT_IN_MILLI + 60 * 1000 * 30;

    RpcServerIdleStateHandler() {
        super(0, 0, IDLE_TIMEOUT_IN_MILLI, TimeUnit.MILLISECONDS);
    }
}
```
`public class RpcServerInitializer extends ChannelInitializer<SocketChannel>`源码如下:
```java
pipeline.addLast(new RpcServerIdleStateHandler());
```
ps：理解上述netty源码需要对netty网络框架有一定的理解。

#### 长连接平滑关闭机制
待补充





