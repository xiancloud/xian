package info.xiancloud.qclouddocker.api.unit.service;

import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;

/**
 * 回滚服务接口
 *
 * @author yyq
 */
public class RollBackClusterServiceUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "rollBackClusterService";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("回滚服务（用于回滚服务至升级前的配置,只能回滚至上一个配置）");
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
        return "RollBackClusterService";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
