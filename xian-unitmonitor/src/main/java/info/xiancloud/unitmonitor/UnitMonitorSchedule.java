package info.xiancloud.unitmonitor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.falcon.DiyMonitorGroup;
import info.xiancloud.core.util.LOG;

import java.util.Map.Entry;

/**
 * @author yyq
 * @deprecated this won't work on asynchronous xian
 */
public class UnitMonitorSchedule implements Unit {
    @Override
    public String getName() {
        return "unitMonitorSchedule";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("获取unit调用次数").setPublic(false)
                .setBroadcast(UnitMeta.Broadcast.create().setSuccessDataOnly(true).setAsync(false))
                .setMonitorEnabled(false);
    }

    @Override
    public Input getInput() {
        return new Input();

    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        JSONArray retArr = new JSONArray();
        for (Entry<String, CountLatchEntity> entry : UnitMonitorAop.countLatches.entrySet()) {
            JSONObject obj = new JSONObject();
            long secondCall = entry.getValue().secondCall;
            LOG.info(String.format(" 接口调用监控统计 %s 调用 了 %s 次", entry.getKey(), secondCall));
            // 计算调用次数,当前累加的调用次数减去上次的调用次数
            obj.put("title", "接口调用监控");
            obj.put("unit", entry.getKey());
            obj.put("value", secondCall);
            obj.put("nodeId", LocalNodeManager.LOCAL_NODE_ID);
            // 重置 调用次数为0
            entry.getValue().secondCall = 0;
            retArr.add(obj);
        }
        handler.handle(UnitResponse.createSuccess(retArr));
    }

    @Override
    public Group getGroup() {
        return DiyMonitorGroup.singleton;
    }

}
