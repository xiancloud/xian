package info.xiancloud.core.message.sender.local;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.*;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.message.LackParamException;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.sender.AbstractAsyncSender;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sender class for Local unit call. We also make this process asynchronous. The mechanism is we submit the request to our business thread pool for execution,
 * and call the callback to sendback the response after execution finished.
 * No exception will be thrown to you due to this local request is submitted to business thread pool for execution.
 * You will get a unit response object with the exception object as its data property if exception occurs.
 * Note that: LocalSender must be used only when the receipt unit is local! Otherwise you will get a unit undefined exception.
 *
 * @author happyyangyuan
 */
class AbstractLocalAsyncSender extends AbstractAsyncSender {

    private final Map<String, Object> originalMap;

    AbstractLocalAsyncSender(UnitRequest request, NotifyHandler callback) {
        super(request, callback);
        unitRequest.getContext().setDestinationNodeId(LocalNodeManager.LOCAL_NODE_ID);
        originalMap = request.getArgMap();
        //we should clone a new local map to avoid the original map elements to be changed.
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
        ThreadPoolManager.execute(() -> {
            /*IdManager.makeSureMsgId(unitRequest.getContext()); No need, because super asyncSender has made sure msg id.*/
            UnitResponse unitResponse;
            long start = System.nanoTime();
            Unit unit = LocalUnitsManager.getLocalUnit(unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit());
            if (unit == null) {
                unitResponse = new UnitUndefinedException(unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit()).toUnitResponse();
            } else {
                Set<Input.Obj> required = getRequired(unit, unitRequest);
                if (!required.isEmpty()) {
                    String[] requiredParamNames = required.stream().map(Input.Obj::getName).collect(Collectors.toList()).toArray(new String[0]);
                    LackParamException lackParamException = new LackParamException(unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit(), requiredParamNames);
                    unitResponse = UnitResponse.error(Group.CODE_LACK_OF_PARAMETER, lackParamException.getLacedParams(), lackParamException.getMessage());
                } else {
                    try {
                        unitResponse = unit.execute(unitRequest);
                    } catch (Throwable anyExceptionCaughtHere) {
                        unitResponse = UnitResponse.exception(anyExceptionCaughtHere);
                    }
                    if (unitResponse == null) {
                        unitResponse = UnitResponse.failure(null, "Null response is returned from: " + Unit.fullName(unitRequest.getContext().getGroup(), unitRequest.getContext().getUnit()));
                        LOG.error(unitResponse);
                    }
                }
            }
            fillResponseContext(unitResponse.getContext());
            UnitResponse finalResponse = unitResponse;
            long cost = (System.nanoTime() - start) / 1000000;
            JSONObject logJson = new JSONObject() {{
                put("group", unitRequest.getContext().getGroup());
                put("unit", unitRequest.getContext().getUnit());
                put(Constant.COST, cost);
                put("unitRequest", originalMap);
                put("unitResponse", finalResponse);
                put("type", "unit");
            }};
            LOG.info(logJson.toJSONString());
            callback.callback(finalResponse);
        });
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

    //note that context data is full-filled step by step, not at once.
    private void fillResponseContext(UnitResponse.Context context) {
        context.setDestinationNodeId(unitRequest.getContext().getSourceNodeId());
        context.setSourceNodeId(LocalNodeManager.LOCAL_NODE_ID);
        context.setMsgId(unitRequest.getContext().getMsgId());
        context.setSsid(unitRequest.getContext().getSsid());
    }

}
