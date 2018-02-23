package info.xiancloud.plugin.unitmonitor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.support.falcon.DiyMonitorGroup;
import info.xiancloud.plugin.util.LOG;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author yyq
 */
public class UnitMonitorSchedule implements Unit {
    @Override
    public String getName() {
        return "unitMonitorSchedule";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("获取unit调用次数").setPublic(false)
                .setBroadcast(UnitMeta.Broadcast.create().setSuccessDataOnly(true).setAsync(false))
                .setMonitorEnabled(false);
    }

    @Override
    public Input getInput() {
        return new Input();

    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
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
        return UnitResponse.success(retArr);
    }

    @Override
    public Group getGroup() {
        return DiyMonitorGroup.singleton;
    }

}
