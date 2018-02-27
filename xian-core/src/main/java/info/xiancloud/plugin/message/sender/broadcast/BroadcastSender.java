package info.xiancloud.plugin.message.sender.broadcast;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.distribution.exception.UnitOfflineException;
import info.xiancloud.plugin.distribution.exception.UnitUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.UnitRouter;
import info.xiancloud.plugin.distribution.service_discovery.UnitInstance;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.sender.AbstractAsyncSender;
import info.xiancloud.plugin.message.sender.local.RoutedLocalAsyncSender;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.util.CloneUtil;
import info.xiancloud.plugin.util.LOG;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * Calling broadcast unit will make this sender come into use.
 * Use {@link UnitMeta#setBroadcast()} {@link UnitMeta#setBroadcast(UnitMeta.Broadcast)} to define a broadcast unit.
 * </p>
 *
 * @author happyyangyuan
 */
public class BroadcastSender extends AbstractAsyncSender {

    public BroadcastSender(UnitRequest request, NotifyHandler handler) {
        super(request, handler);
    }

    @Override
    protected void asyncSend() throws UnitOfflineException, UnitUndefinedException {
        List<UnitInstance> list = UnitRouter.singleton.allInstances(Unit.fullName(unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit()));
        UnitMeta.Broadcast broadcast = list.get(0).getPayload().getMeta().getBroadcast();
        final CountDownLatch latch = new CountDownLatch(list.size());
        Collection<Object> piledUpOutput = new ConcurrentLinkedQueue<>();
        final NotifyHandler tmpHandler = new NotifyHandler() {
            protected void handle(UnitResponse output) {
                if (!broadcast.isSuccessDataOnly()) {
                    piledUpOutput.add(output);
                } else if (output.succeeded()) {
                    piledUpOutput.add(output.getData());
                }
                latch.countDown();
            }
        };
        for (UnitInstance unitInstance : list) {
            if (unitInstance.getNodeId().equals(LocalNodeManager.LOCAL_NODE_ID)) {
                //we must not share the same unitRequest object while sending request concurrently.
                UnitRequest clonedUnitRequest = CloneUtil.cloneBean(unitRequest, UnitRequest.class);
                /*clonedUnitRequest.getContext().setDestinationNodeId(unitInstance.getNodeId()); no need  */
                new RoutedLocalAsyncSender(clonedUnitRequest, tmpHandler).send();
            } else {
                LOG.debug("In order not to share the same unit request object，we clone a new request");
                UnitRequest clonedRequest = CloneUtil.cloneBean(unitRequest, UnitRequest.class);
                clonedRequest.getContext().setDestinationNodeId(unitInstance.getNodeId());
                LocalNodeManager.send(clonedRequest, tmpHandler);
            }
        }
        if (broadcast.isAsync()) {
            callback.callback(UnitResponse.success());
        } else {
            try {
                if (!latch.await(broadcast.getTimeoutInMilli(), TimeUnit.MILLISECONDS)) {
                    LOG.error(new TimeoutException());
                    callback.callback(UnitResponse.error(Group.CODE_TIME_OUT, piledUpOutput,
                            "Time out while waiting for all the units to response, the data is only part of the result. "));
                } else {
                    callback.callback(UnitResponse.success(piledUpOutput));
                }
            } catch (InterruptedException e) {
                callback.callback(UnitResponse.exception(e));
            }
        }
    }

}
