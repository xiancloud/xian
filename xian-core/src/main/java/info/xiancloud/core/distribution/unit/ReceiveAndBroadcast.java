package info.xiancloud.core.distribution.unit;

import info.xiancloud.core.*;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.exception.UnitOfflineException;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.message.RequestContext;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 接收请求并将其广播出去的unit，特别的，它可以将所有接收方的响应聚合返回给调用者
 *
 * @author happyyangyuan
 * @deprecated 此方法目前不支持传'application'以外的参数，请直接实现Unit接口，设置{@link UnitMeta#setBroadcast()}替代方案
 */
public abstract class ReceiveAndBroadcast implements Unit {
    private static final String ALL = "all";

    @Override
    public Input getInput() {
        return new Input()
                .add("application", String.class, "all/null/applicationName  ，  为空或者为all表示全部节点");
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        if (msg.getContext().isRouted()) {
            handler.handle(execute0(msg));
            return;
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
                handler.handle(UnitResponse.createSuccess());
                return;
            } else {
                try {
                    latch.await(timeoutInMilli(), TimeUnit.MILLISECONDS);
                    handler.handle(UnitResponse.createSuccess(piledUpOutput));
                    return;
                } catch (InterruptedException e) {
                    handler.handle(UnitResponse.createException(e));
                    return;
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
