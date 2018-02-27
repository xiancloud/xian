package info.xiancloud.plugin.distribution.unit;

import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.exception.UnitOfflineException;
import info.xiancloud.plugin.distribution.exception.UnitUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.UnitRouter;
import info.xiancloud.plugin.distribution.service_discovery.UnitInstance;
import info.xiancloud.plugin.message.RequestContext;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 接收请求并将其广播出去的unit，特别的，它可以将所有接收方的响应聚合返回给调用者
 * 此方法目前不支持传'application'以外的参数，请直接实现Unit接口，设置{@link UnitMeta#setBroadcast()}替代方案
 *
 * @author happyyangyuan
 */
public abstract class ReceiveAndBroadcast implements Unit {
    private static final String ALL = "all";

    @Override
    public Input getInput() {
        return new Input()
                .add("application", String.class, "all/null/applicationName  ，  为空或者为all表示全部节点");
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        if (msg.getContext().isRouted()) {
            return execute0(msg);
        } else {
            List<UnitInstance> list = new ArrayList<>();
            String application = msg.get("application", String.class);
            List<UnitInstance> unitInstances;
            try {
                unitInstances = UnitRouter.singleton.allInstances(Unit.fullName(getGroupName(), getUnitName()));
            } catch (UnitOfflineException | UnitUndefinedException e) {
                throw new RuntimeException(e);
            }
            if (StringUtil.isEmpty(application) || ALL.equals(application)) {
                list.addAll(unitInstances);
            } else {
                for (UnitInstance clientInfo : unitInstances) {
                    if (clientInfo.getName().equals(msg.getString("application"))) {
                        list.add(clientInfo);
                    }
                }
            }
            CountDownLatch latch = new CountDownLatch(list.size());
            List<Object> piledUpOutput = new ArrayList<>();
            for (UnitInstance clientInfo : list) {
                LocalNodeManager.send(
                        new UnitRequest().setContext(
                                RequestContext.create().setGroup(getGroupName()).
                                        setUnit(getUnitName())
                                        .setDestinationNodeId(clientInfo.getNodeId())
                        ),
                        new NotifyHandler() {
                            protected void handle(UnitResponse unitResponse) {
                                LOG.info("对" + clientInfo.getNodeId() + "执行" + getName() + "操作完毕");
                                if (!successDataOnly()) {
                                    piledUpOutput.add(unitResponse);
                                } else if (unitResponse.succeeded()) {
                                    piledUpOutput.add(unitResponse.getData());
                                }
                                latch.countDown();
                            }
                        });
            }
            if (async()) {
                return UnitResponse.success();
            } else {
                try {
                    latch.await(timeoutInMilli(), TimeUnit.MILLISECONDS);
                    return UnitResponse.success(piledUpOutput);
                } catch (InterruptedException e) {
                    return UnitResponse.exception(e);
                }
            }
        }
    }

    /**
     * 本节点执行的动作
     */
    abstract protected UnitResponse execute0(UnitRequest msg);

    /**
     * @return 是否异步执行
     */
    protected boolean async() {
        LOG.debug("defaults to asynchronous");
        return true;
    }

    /**
     * @return 在同步执行时的超时时间
     */
    protected long timeoutInMilli() {
        return 5000;
    }

    /**
     * 注意，该属性只在同步模式下才生效
     *
     * @return true 返回结果是data集；false 返回unitResponse集；默认为false；
     */
    protected boolean successDataOnly() {
        return false;
    }

    private String getUnitName() {
        return getName();
    }

    private String getGroupName() {
        return getGroup().getName();
    }

}
