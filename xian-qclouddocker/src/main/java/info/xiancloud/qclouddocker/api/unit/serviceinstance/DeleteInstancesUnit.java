package info.xiancloud.qclouddocker.api.unit.serviceinstance;

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

/**
 * 删除服务实例接口
 *
 * @author yyq
 */
public class DeleteInstancesUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "deleteInstances";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("删除服务实例");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("clusterId", String.class, "集群ID，可通过查询集群接口反回字段中的 clusterId获取", REQUIRED)
                .add("instances.n", String[].class, "实例名称数组,请使用查询服务实例列表中返回的name字段", REQUIRED)
                .add("namespace", String.class, "命名空间,默认为default");
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
                    if (arg.getClazz().isPrimitive() || arg.getClazz() == String.class) {
                        params.put(arg.getName(), msg.getArgMap().get(arg.getName()) + "");
                    } else {
                        // 复合类型参数
                        JSONArray jsonArr = JSON.parseArray(msg.getArgMap().get(arg.getName()).toString());
                        switch (arg.getName()) {
                            case "instances.n":
                                for (int i = 0; i < jsonArr.size(); i++) {
                                    String prefix = String.format("instances.%s", i);
                                    params.put(prefix, jsonArr.getString(i));
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
        return "DeleteInstances";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
