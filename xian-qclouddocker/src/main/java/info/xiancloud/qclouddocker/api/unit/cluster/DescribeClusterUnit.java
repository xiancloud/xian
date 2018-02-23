package info.xiancloud.qclouddocker.api.unit.cluster;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.message.UnitRequest;
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
        return UnitMeta.create("查询集群列表");
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
