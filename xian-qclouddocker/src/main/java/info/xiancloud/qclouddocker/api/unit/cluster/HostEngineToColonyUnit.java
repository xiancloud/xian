package info.xiancloud.qclouddocker.api.unit.cluster;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
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
        return UnitMeta.createWithDescription("添加已存在云主机到集群");
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
