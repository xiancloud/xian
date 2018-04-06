package info.xiancloud.qclouddocker.api.unit.service;

import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;

/**
 * 查询服务列表
 *
 * @author yyq
 */
public class DescribeClusterServiceUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "describeClusterService";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("查询服务列表");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("clusterId", String.class, "集群ID，可通过查询集群接口反回字段中的 clusterId获取", REQUIRED)
                .add("namespace", String.class, "命名空间,不传默认为default")
                .add("allnamespace", int.class, "是否展示所有命名空间下的服务。1是，0或不传为否");
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return super.execute(msg);
    }

    @Override
    public String getAction() {
        return "DescribeClusterService";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
