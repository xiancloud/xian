package info.xiancloud.qclouddocker.api.unit.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;

import java.util.List;
import java.util.TreeMap;

public class GetMonitorDataUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "getMonitorData";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("查询服务列表");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("namespace", String.class, "命名空间", REQUIRED)
                .add("metricName", int.class, "指标名称", REQUIRED)
                .add("dimensions.n.name", String[].class, "维度的名称，与dimensions.n.value配合使用", REQUIRED)
                .add("dimensions.n.value", String[].class, "对应的维度的值，与dimensions.n.name配合使用", REQUIRED)
                .add("period", int.class, "对应的维度的值，与dimensions.n.name配合使用")
                .add("startTime", String.class, "起始时间，如 2016-01-01 10:25:00 。 默认时间为当天的 00:00:00")
                .add("endTime", String.class, "结束时间，默认为当前时间。 endTime不能小于startTime");
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return super.execute(msg);
    }

    @Override
    protected void fillUnitArgs(UnitRequest msg, TreeMap<String, String> params) {

        // 添加接口请求参数
        List<Input.Obj> argList = this.getInput().getList();
        if (argList != null && !argList.isEmpty()) {
            argList.forEach(arg -> {
                if (!StringUtil.isEmpty(msg.getArgMap().get(arg.getName()))) {
                    // 基本类型
                    if (arg.getClazz().isPrimitive()) {
                        params.put(arg.getName(), msg.getArgMap().get(arg.getName()) + "");
                    } else {
                        // 复合类型参数
                        JSONArray jsonArr = JSON.parseArray(msg.getArgMap().get(arg.getName()).toString());
                        switch (arg.getName()) {
                            case "dimensions.n.name":
                                //获取维度值数组
                                JSONArray valueArr = JSON.parseArray(msg.getArgMap().get("dimensions.n.value").toString());
                                for (int i = 0; i < jsonArr.size(); i++) {
                                    // 参数前缀
                                    String prefix = String.format("dimensions.%s.", i);
                                    params.put(prefix + "name", jsonArr.getString(i));
                                    params.put(prefix + "value", valueArr.getString(i));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }
    }

    @Override
    public String getAction() {
        return "GetMonitorData";
    }

    @Override
    public String getAPIHost() {
        return "monitor.api.qcloud.com";
    }

}
