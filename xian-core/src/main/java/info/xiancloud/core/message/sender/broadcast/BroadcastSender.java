package info.xiancloud.core.message.sender.broadcast;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.exception.UnitOfflineException;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.sender.AbstractAsyncSender;
import info.xiancloud.core.message.sender.local.RoutedLocalAsyncSender;
import info.xiancloud.core.util.CloneUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.ListenableCountDownLatch;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        List<UnitInstance> list = UnitRouter.SINGLETON.allInstances(Unit.fullName(unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit()));
        UnitMeta.Broadcast broadcast = list.get(0).getPayload().getMeta().getBroadcast();
        final ListenableCountDownLatch latch = new ListenableCountDownLatch(list.size());
        ConcurrentLinkedQueue<Object> piledUpOutput = new ConcurrentLinkedQueue<>();
        latch.addListener(counter -> {
            if (counter == 0) {
                callback.callback(UnitResponse.createSuccess(piledUpOutput));
            } else {
                LOG.debug("Not all responses are sent back.");
                //todo What if some responses never come back? How to deal with timeout?
                //todo Maybe we need a timeout property in the notify handler.
            }
        });
        final NotifyHandler tmpHandler = new NotifyHandler() {
            protected void handle(UnitResponse output) {
                if (!broadcast.isSuccessDataOnly()) {
                    piledUpOutput.add(output);
                } else if (output.succeeded()) {
                    piledUpOutput.add(output.getData());
                } else {
                    LOG.debug("Failed response is ignored here.");
                }
                latch.countDown();
            }
        };
        if (list.size() > 100) {
            LOG.warn(String.format("Too many unit instances of %s.%s found! Take care on the performance!",
                    unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit()));
        }
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
        /*try {
            if (!latch.await(broadcast.getTimeoutInMilli(), TimeUnit.MILLISECONDS)) {
                LOG.error(new TimeoutException());
                callback.callback(UnitResponse.createError(Group.CODE_TIME_OUT, piledUpOutput,
                        "Time out while waiting for all the units to response, the data is only part of the result. "));
            } else {
                callback.callback(UnitResponse.createSuccess(piledUpOutput));
            }
        } catch (InterruptedException e) {
            callback.callback(UnitResponse.createException(e));
        }*/
    }

}
