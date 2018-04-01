package info.xiancloud.core.message.sender.broadcast;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.exception.UnitOfflineException;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.Group;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.sender.AbstractAsyncSender;
import info.xiancloud.core.message.sender.local.RoutedLocalAsyncSender;
import info.xiancloud.core.util.CloneUtil;
import info.xiancloud.core.util.LOG;

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
            callback.callback(UnitResponse.createSuccess());
        } else {
            try {
                if (!latch.await(broadcast.getTimeoutInMilli(), TimeUnit.MILLISECONDS)) {
                    LOG.error(new TimeoutException());
                    callback.callback(UnitResponse.createError(Group.CODE_TIME_OUT, piledUpOutput,
                            "Time out while waiting for all the units to response, the data is only part of the result. "));
                } else {
                    callback.callback(UnitResponse.createSuccess(piledUpOutput));
                }
            } catch (InterruptedException e) {
                callback.callback(UnitResponse.createException(e));
            }
        }
    }

}
