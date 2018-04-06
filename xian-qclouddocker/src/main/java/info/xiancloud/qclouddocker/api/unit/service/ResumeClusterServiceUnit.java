package info.xiancloud.qclouddocker.api.unit.service;

import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;

/**
 * 继续服务更新
 *
 * @author yyq
 */
public class ResumeClusterServiceUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "resumeClusterService";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("继续服务更新(用于继续被暂停中的服务更新)");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("clusterId", String.class, "集群ID，可通过查询集群接口反回字段中的 clusterId获取", REQUIRED)
                .add("serviceName", String.class, "服务名", REQUIRED)
                .add("namespace", String.class, "命名空间,默认为default");
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return super.execute(msg);
    }

    @Override
    public String getAction() {
        return "ResumeClusterService";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
