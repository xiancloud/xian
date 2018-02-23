package info.xiancloud.qclouddocker.api.unit.cluster;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.qclouddocker.api.service.QcloudContainerGroup;

/**
 * 添加已存在云主机到集群
 *
 * @author yyq
 * @deprecated Not supported yet.
 */
public class HostEngineToColonyUnit implements Unit {
    @Override
    public String getName() {
        return "addClusterInstancesFromExistedCvm";
    }

    @Override
    public Group getGroup() {
        return QcloudContainerGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("添加已存在云主机到集群");
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        throw new RuntimeException("Not supported yet!");
    }

}
