package info.xiancloud.qclouddocker.api.unit.serviceinstance;

import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;

/**
 * 查询服务实例列表接口
 *
 * @author yyq
 */
public class DescribeServiceInstanceUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "describeServiceInstance";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("查询服务实例列表");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("clusterId", String.class, "集群ID，可通过查询集群接口反回字段中的 clusterId获取", REQUIRED)
                .add("serviceName", String.class, "服务名", REQUIRED)
                .add("offset", int.class, "偏移量,默认0")
                .add("limit", int.class, "最大输出条数，默认20")
                .add("namespace", String.class, "命名空间,默认为default");
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return super.execute(msg);
    }

    @Override
    public String getAction() {
        return "DescribeServiceInstance";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
