package info.xiancloud.qclouddocker.api.unit.service;

import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;

/**
 * 修改服务描述接口
 *
 * @author yyq
 */
public class ModifyServiceDescriptionUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "modifyServiceDescription";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("修改服务描述");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("clusterId", String.class, "集群ID，可通过查询集群接口反回字段中的 clusterId获取", REQUIRED)
                .add("serviceName", String.class, "服务名", REQUIRED)
                .add("description", String.class, "服务描述，空或者不填将消除服务描述")
                .add("namespace", String.class, "命名空间,默认为default");
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return super.execute(msg);
    }

    @Override
    public String getAction() {
        return "ModifyServiceDescription";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
