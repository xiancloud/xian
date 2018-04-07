package info.xiancloud.mq.backed;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Handler;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.MessageType;
import info.xiancloud.core.distribution.UnitJudge;
import info.xiancloud.core.distribution.UnitProxy;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.core.init.IStartService;
import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.message.IdManager;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.mq.IMqConsumerClient;
import info.xiancloud.core.mq.TransferQueueUtil;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;

/**
 * requests for transferable unit are all transferred from here.
 *
 * @author happyyangyuan
 */
public class TransferManager implements IStartService, ShutdownHook {

    private static final Set<String> queueNames = Collections.synchronizedSet(new HashSet<>());

    private ScheduledFuture scheduledFuture;

    public boolean startup() {
        scheduledFuture = ThreadPoolManager.scheduleWithFixedDelay(() -> {
            for (String unitFullName : UnitDiscovery.singleton.queryForNames()) {
                try {
                    UnitProxy unitProxy = UnitRouter.singleton.newestDefinition(unitFullName);
                    String group = Unit.parseFullName(unitFullName).fst;
                    if (unitProxy.getMeta().isTransferable()) {
                        startIfNotStarted(group);
                    }
                } catch (UnitUndefinedException ignored) {
                } catch (Throwable unknownException) {
                    LOG.error("未知异常，跳过：" + unitFullName, unknownException);
                }
            }
        }, 1000 * 120);
        //定期刷新动作，是为了应对新的transfer unit随时可以加入，而不需要重启
        return true;
    }

    public boolean shutdown() {
        synchronized (queueNames) {
            if (scheduledFuture != null)
                scheduledFuture.cancel(false);
            for (String service : queueNames) {
                stopIfStarted(service);
            }
            LOG.info("已取消订阅所有堆积队列");
            return true;
        }
    }

    /**
     * 启动中转mqtt客户端；
     * 注意：1、中转mqtt客户端是单例的。
     * 2、可以重复调用，如果已经启动，则不再启动。
     *
     * @param serviceName 需要做消息可靠保证的业务名
     */
    private static void startIfNotStarted(String serviceName) {
        synchronized (queueNames) {
            if (!queueNames.contains(serviceName)) {
                //以下是新建一个rabbitmq客户端
                try {
                    IMqConsumerClient.singleton.consumeStaticQueue(TransferQueueUtil.getTransferQueue(serviceName), callback);
                    queueNames.add(serviceName);
                } catch (Throwable e) {
                    LOG.warn("订阅静态队列失败：" + serviceName, e);
                }
            }
        }
    }

    /**
     * 停止中转mqtt客户端；
     * 注意：1、中转mqtt客户端是单例的。
     * 2、可以重复调用，如果已经启动，则不再启动。
     *
     * @param groupName 定义了transferableUnit的Group名
     */
    private static void stopIfStarted(String groupName) {
        synchronized (queueNames) {
            if (queueNames.contains(groupName)) {
                IMqConsumerClient.singleton.unconsume(TransferQueueUtil.getTransferQueue(groupName));
            }
        }
    }

    //单例的订阅者callback
    private static final Function<JSONObject, Boolean> callback = originalMsg -> {
        try {
            IdManager.makeSureMsgId(originalMsg);
            if (!MessageType.isDefaultRequest(originalMsg)) {
                LOG.warn(String.format("非%s消息忽略不处理：%s", MessageType.request, MessageType.getMessageType(originalMsg)));
                return true;
            }
            UnitRequest request = originalMsg.toJavaObject(UnitRequest.class);
            String group = request.getContext().getGroup(),
                    unit = request.getContext().getUnit();
            if (StringUtil.isEmpty(group) && !StringUtil.isEmpty(unit)) {
                LOG.warn("本持久化队列只支持unitRequest消息分发");
                return true;
            }
            while (!UnitJudge.available(group, unit)) {
                LOG.info(new JSONObject() {{
                    put("type", "transfer");
                    put("event", "pileup");
                    put("queue", group);
                    put("unit", unit);
                    put("description", "目标unit不在线，队列堆积");
                }});
                Thread.sleep(10 * 1000);
            }
            Handler<UnitResponse> callback = response -> {
                LOG.info("中转器收到回调:" + response + ";准备转发回调结果至原始发送端...");
                LocalNodeManager.sendBack(response);
                //todo 建议在这里做队列ack，既可以实现推送速度控制，也可以保证消息成功处理才从队列删除消息
            };
            LOG.debug("发送给unit的消息必须使用Xian发送，它支持unit多样性发送方式，必须标记为transferredAlready否则会出现循环消息");
            request.getContext().setTransferredAlready(true);
            SingleRxXian.call(request).subscribe(callback::handle);
            return true;
        } catch (Throwable t) {
            LOG.error(t);
            return false;
        } finally {
            MsgIdHolder.clear();
        }
    };
}
