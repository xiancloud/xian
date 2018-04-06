package info.xiancloud.qclouddocker.api.unit.cluster;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;
import info.xiancloud.qclouddocker.api.service.QcloudContainerGroup;

/**
 * 查询集群列表
 *
 * @author yyq
 */
public class DescribeClusterUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "describeCluster";
    }

    @Override
    public Group getGroup() {
        return QcloudContainerGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("查询集群列表");
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return super.execute(msg);
    }

    @Override
    protected String getAction() {
        return "DescribeCluster";
    }

    @Override
    protected String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
