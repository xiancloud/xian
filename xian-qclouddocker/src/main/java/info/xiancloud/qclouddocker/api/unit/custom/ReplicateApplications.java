package info.xiancloud.qclouddocker.api.unit.custom;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.qclouddocker.api.service.QcloudContainerGroup;

/**
 * 修改实例副本数
 *
 * @author happyyangyuan
 */
public class ReplicateApplications implements Unit {
    @Override
    public String getName() {
        return "replicateApplications";
    }

    @Override
    public Group getGroup() {
        return QcloudContainerGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("修改实例副本数");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("applications", String.class, "应用名列表,eg. gateway=1,gatewayOut=2,someApp=0", REQUIRED)
                .add("jobName", String.class, "jenkins项目名", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String jobName = msg.get("jobName", String.class),
                env = jobName.split("_")[jobName.split("_").length - 1];
        String application_replicas = msg.getString("applications");
        if (StringUtil.isEmpty(application_replicas)) {
            return UnitResponse.createUnknownError(null, "Nothing changed.").setContext(UnitResponse.Context.create().setPretty(true));
        }
        try {
            return UnitResponse.createSuccess(DeploymentUtil.replicate(application_replicas, env)).setContext(UnitResponse.Context.create().setPretty(true));
        } catch (DeploymentUtil.CloudApiFailedException e) {
            return UnitResponse.createException(e, "调用容器API失败").setContext(UnitResponse.Context.create().setPretty(true));
        } catch (IllegalArgumentException e) {
            return UnitResponse.createException(e).setContext(UnitResponse.Context.create().setPretty(true));
        }

    }
}
