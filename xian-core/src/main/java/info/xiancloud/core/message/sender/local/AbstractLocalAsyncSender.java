package info.xiancloud.core.message.sender.local;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.message.LackParamException;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.sender.AbstractAsyncSender;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;

/**
 * Sender class for Local unit call. We also make this process asynchronous. The mechanism is we submit the request to our business thread pool for execution,
 * and call the callback to send back the response after execution finished.
 * No exception will be thrown to you due to this local request is submitted to business thread pool for execution.
 * You will get a unit response object with the exception object as its data property if exception occurs.
 * Note that: LocalSender must be used only when the receipt unit is local! Otherwise you will get a unit undefined exception.
 *
 * @author happyyangyuan
 */
class AbstractLocalAsyncSender extends AbstractAsyncSender {

    AbstractLocalAsyncSender(UnitRequest request, NotifyHandler callback) {
        super(request, callback);
        unitRequest.getContext().setDestinationNodeId(LocalNodeManager.LOCAL_NODE_ID);
        Map<String, Object> originalMap = request.getArgMap();
        //we clone a new local map to avoid the original map elements being changed.
        Map<String, Object> clonedMap = new HashMap<>();
        if (originalMap != null) {
            for (String s : originalMap.keySet()) {
                clonedMap.put(s, originalMap.get(s));
            }
        }
        unitRequest.setArgMap(clonedMap);
    }

    @Override
    protected void asyncSend() {
        final long start = System.nanoTime();
        Unit unit = LocalUnitsManager.getLocalUnit(unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit());
        if (unit == null) {
            UnitResponse unitResponse = new UnitUndefinedException(unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit()).toUnitResponse();
            responseCallback(unitResponse, start);
        } else {
            Set<Input.Obj> required = getRequired(unit, unitRequest);
            if (!required.isEmpty()) {
                String[] requiredParamNames = required.stream().map(Input.Obj::getName).toArray(String[]::new);
                LackParamException lackParamException = new LackParamException(unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit(), requiredParamNames);
                UnitResponse unitResponse = UnitResponse.createError(Group.CODE_LACK_OF_PARAMETER, lackParamException.getLacedParams(), lackParamException.getMessage());
                responseCallback(unitResponse, start);
            } else {
                ScheduledFuture future = timeoutAfter(unitRequest.getContext().getTimeOutInMilli(), start);
                ThreadPoolManager.execute(() -> {
                    //fixme. Use thread pool to commit the async unit unless/until you can make sure all unit is asynchronously executed.
                    // we don't know whether the unit execution is asynchronous or blocking
                    // so here we submit the task to the thread pool for execution to make it 100% asynchronous.
                    // And let the caller to decide whether or not to block.
                    try {
                        unit.execute(unitRequest, unitResponse -> {
                            boolean msgIdWritten = MsgIdHolder.set(unitResponse.getContext().getMsgId());
                            try {
                                future.cancel(true);
                                responseCallback(unitResponse, start);
                            } finally {
                                if (msgIdWritten) {
                                    MsgIdHolder.clear();
                                }
                            }
                        });
                    } catch (Throwable e) {
                        future.cancel(true);
                        responseCallback(UnitResponse.createException(e), start);
                    }
                });
            }
        }
    }

    private ScheduledFuture timeoutAfter(long timeoutInMilli, long start) {
        return ThreadPoolManager.schedule(() -> {
            if (callback.getTimeout() == null) {
                responseCallback(UnitResponse.createTimeout(new TimeoutException(), String.format("unit执行超时，%s ms", timeoutInMilli)), start);
                // If the stateful callback object's timeout property is empty which means it is not called back nor timed out either.
                callback.setTimeout(true);
            }
        }, timeoutInMilli, MsgIdHolder.get());
    }

    private static Set<Input.Obj> getRequired(Unit recipient, UnitRequest unitRequest) {
        Set<Input.Obj> required = new HashSet<>();
        if (recipient.getInput() != null && !recipient.getInput().getList().isEmpty()) {
            for (Input.Obj obj : recipient.getInput().getList()) {
                if (obj.isRequired() && StringUtil.isEmpty(unitRequest.get(obj.getName()))) {
                    required.add(obj);
                }
            }
        }
        return required;
    }

    private void responseCallback(UnitResponse unitResponse, long start/*, Boolean timeout*/) {
        fillResponseContext(unitResponse.getContext());
        long cost = (System.nanoTime() - start) / 1000000;
        JSONObject logJson = new JSONObject().
                fluentPut("group", unitRequest.getContext().getGroup()).
                fluentPut("unit", unitRequest.getContext().getUnit()).
                fluentPut(Constant.COST, cost).
                fluentPut("unitRequest", unitRequest).
                fluentPut("unitResponse", unitResponse).
                fluentPut("type", "unit");
        LOG.info(logJson);
        callback.callback(unitResponse);
    }

    /**
     * note that context data is full-filled step by step, not at once.
     */
    private void fillResponseContext(UnitResponse.Context context) {
        context.setDestinationNodeId(unitRequest.getContext().getSourceNodeId());
        context.setSourceNodeId(LocalNodeManager.LOCAL_NODE_ID);
        context.setMsgId(unitRequest.getContext().getMsgId());
        context.setSsid(unitRequest.getContext().getSsid());
    }

}
