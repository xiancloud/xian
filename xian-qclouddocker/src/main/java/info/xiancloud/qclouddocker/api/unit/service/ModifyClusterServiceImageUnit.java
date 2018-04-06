package info.xiancloud.qclouddocker.api.unit.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;

import java.util.List;
import java.util.TreeMap;

/**
 * 更新服务镜像
 *
 * @author yyq
 */
public class ModifyClusterServiceImageUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "modifyClusterServiceImage";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription(" 更新服务镜像");
    }

    @Override
    public Input getInput() {
        return new Input().add("clusterId", String.class, "集群ID，可通过查询集群接口反回字段中的 clusterId获取", REQUIRED)
                .add("serviceName", String.class, "要更新的服务名", REQUIRED)
                .add("image", String.class, "新镜像，如果服务中一个实例下只有一个container可以传此参数(image和containers二者必填一个)")
                .add("containers.n", String[].class, "新镜像，如果服务中一个实例下有多个container需要传入此参数指定需要修改的container的name和对应的image(image和containers二者必填一个)")
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
                            case "containers.n":
                                for (int i = 0; i < jsonArr.size(); i++) {
                                    String prefix = String.format("containers.%s.", i);
                                    JSONObject obj = jsonArr.getJSONObject(i);
                                    params.put(prefix + "containerName", obj.getString("containerName"));
                                    params.put(prefix + "image", obj.getString("image"));
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
        return "ModifyClusterServiceImage";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
