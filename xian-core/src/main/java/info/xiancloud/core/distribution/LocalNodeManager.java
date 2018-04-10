package info.xiancloud.core.distribution;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import info.xiancloud.core.Constant;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.loadbalance.ApplicationRouter;
import info.xiancloud.core.distribution.loadbalance.GroupRouter;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.message.IdManager;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 本地节点管理，本类维护着一个全局的本地单例节点{@link Node}<br>
 * 注意：{@link Node}不具备负载均衡以及节点路由功能，也不应该具备！<br>
 * 提示：负载均衡的能力存在于 {@link UnitRouter} {@linkplain ApplicationRouter} {@link GroupRouter}
 *
 * @author happyyangyuan
 */
public class LocalNodeManager {
    public static final Cache<String, NotifyHandler> handleMap = CacheBuilder.newBuilder()
            .expireAfterAccess(24, TimeUnit.HOURS)//扩大过期时间，避免回调消息丢失，任务不允许超过24小时还未执行完毕。
            .maximumSize(1000 * 10000)
            .removalListener(new RemovalListener<String, NotifyHandler>() {
                public void onRemoval(RemovalNotification<String, NotifyHandler> notification) {
                    String description;
                    switch (notification.getCause()) {
                        case REPLACED:
                            description = "出现重复的ssid的notifyHandler,原ssid对应的notifyHandler被移除，ssid=" + notification.getKey();
                            LOG.error(new JSONObject() {{
                                put("type", "notifyHandlerMapRemoval");
                                put("mapSize", handleMap.size());
                                put("cause", notification.getCause().name());
                                put("ssid", notification.getKey());
                                put("notifyHandler", notification.getValue());
                                put("description", description);
                            }});
                            break;
                        case EXPIRED:
                            description = "notifyHandler已过期:" + notification.getKey();
                            LOG.info(new JSONObject() {{
                                put("type", "notifyHandlerMapRemoval");
                                put("mapSize", handleMap.size());
                                put("cause", notification.getCause().name());
                                put("ssid", notification.getKey());
                                put("notifyHandler", notification.getValue());
                                put("description", description);
                            }});
                            break;
                        case SIZE:
                            description = "notifyHandlerMap的size超过上限，可能是内存泄漏";
                            LOG.info(new JSONObject() {{
                                put("type", "notifyHandlerMapRemoval");
                                put("mapSize", handleMap.size());
                                put("cause", notification.getCause().name());
                                put("ssid", notification.getKey());
                                put("notifyHandler", notification.getValue());
                                put("description", description);
                            }});
                            break;
                        default:
                            LOG.debug(new JSONObject() {{
                                put("type", "notifyHandlerMapRemoval");
                                put("mapSize", handleMap.size());
                                put("cause", notification.getCause().name());
                                put("ssid", notification.getKey());
                                put("notifyHandler", notification.getValue());
                                put("description", "正常删除");
                            }});
                    }
                }
            })
            .build();

    /**
     * 每个xian节点都有唯一localNodeId
     */
    public static final String LOCAL_NODE_ID = IdManager.LOCAL_NODE_ID;

    public static INode singleton = new Node(LOCAL_NODE_ID);

    /**
     * This method is only for internal usage.
     * Please use {@link #send(UnitRequest, NotifyHandler)} instead.
     *
     * @param request the message object. The destination node id must be indicated in this unit request object's context.
     * @param handler the callback handler.
     */
    public static void sendLoadBalanced(UnitRequest request, NotifyHandler handler) {
        singleton.send(request, handler);
    }

    /**
     * 定向发送消息
     * TODO Here we need to handle the timeout if the response never comes back.
     */
    public static void send(UnitRequest unitRequest, NotifyHandler handler) {
        unitRequest.getContext().setRouted(true);
        singleton.send(unitRequest, handler);
    }

    public static void sendBack(UnitResponse response/* 不再支持双向长连接了原因请参考teambition, ChannelHandlerContext onlyRpcUseCtx*/) {
        singleton.sendBack(response);
    }

    public static void sendBack(UnitResponse response, Consumer<String> backPayloadConsumerOnFailure) {
        singleton.sendBack(response, backPayloadConsumerOnFailure);
    }

    synchronized public static void destroy() {
        if (singleton != null) {
            singleton.destroy();
            singleton = null;
        }
    }

    synchronized public static void init() {
        try {
            singleton.init();
        } catch (Throwable e) {
            LOG.error("本地节点初始化失败，系统退出：" + Constant.SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR, e);
            System.exit(Constant.SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR);
        }
    }

}
