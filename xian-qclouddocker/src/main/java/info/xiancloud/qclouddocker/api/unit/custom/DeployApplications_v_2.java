package info.xiancloud.qclouddocker.api.unit.custom;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.qclouddocker.api.service.QcloudContainerGroup;

import java.util.List;

/**
 * 更新/新建application，不删除服务，删除服务需要在腾讯云后台进行
 *
 * @author happyyangyuan
 */
public class DeployApplications_v_2 implements Unit {
    @Override
    public String getName() {
        return "deployApplications_v_2";
    }

    @Override
    public Group getGroup() {
        return QcloudContainerGroup.singleton;
    }

    private final String NOT_SUPPORTED_TIP = getName() + "不再支持修改副本数量";

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("更新指定环境指定镜像下的指定服务；注意，" + NOT_SUPPORTED_TIP);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("applicationsInXianRuntime", String.class, "由脚本扫描xian_runtime包内的自路径列别得到，逗号分隔", REQUIRED)
                .add("applications", String.class, "应用名列表，以逗号分隔，如果为空则什么也不做")
                .add("jobName", String.class, "jenkins项目名", REQUIRED)
                .add("buildNumber", String.class, "Jenkins构建编号", REQUIRED)
                ;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String applications = msg.getString("applications");
        UnitResponse unitResponseObject;
        if (applications != null && applications.contains("=")) {
            unitResponseObject = UnitResponse.createUnknownError(null, NOT_SUPPORTED_TIP);
        } else
            unitResponseObject = deploy(msg);
        unitResponseObject.getContext().setPretty(true);
        return unitResponseObject;
    }

    //原发布逻辑
    private UnitResponse deploy(UnitRequest msg) {
        String jobName = msg.get("jobName", String.class),
                buildNumber = msg.get("buildNumber", String.class),
                env = DeploymentUtil.envByJobName(jobName),
                applicationsInXianRuntime = msg.getString("applicationsInXianRuntime"),
                applicationsString = msg.getString("applications");
        List<String> envRunningServices = DeploymentUtil.envRunningService(env);
        List<String> applicationsToDeploy = DeploymentUtil.parseApplicationString(applicationsString);
        DeploymentUtil.checkApplicationsExits(applicationsToDeploy, DeploymentUtil.parseApplicationString(applicationsInXianRuntime));
        List<String> applicationsToUpdate = DeploymentUtil.applicationsToUpdate(applicationsToDeploy, envRunningServices, env);
        for (String applicationToUpdate : applicationsToUpdate) {
            DeploymentUtil.updateService(buildNumber, jobName, applicationToUpdate);
        }
        List<String> applicationsToCreate = DeploymentUtil.applicationsToCreate(applicationsToDeploy, envRunningServices, env);
        for (String application : applicationsToCreate) {
            DeploymentUtil.createService(jobName, application, buildNumber);
        }
        return UnitResponse.createSuccess(new JSONObject() {{
            put("newImage", DeploymentUtil.image(jobName, buildNumber));
            put("runningServices", envRunningServices);
            put("applicationsToUpdate", applicationsToUpdate);
            put("applicationsToCreate", applicationsToCreate);
        }});
    }

    /**
     * 修改实例数量，设置为0表示停xian服务
     *
     * @deprecated {@link #NOT_SUPPORTED_TIP}
     */
    private static UnitResponse replica(UnitRequest msg) {
        String jobName = msg.get("jobName", String.class),
                env = jobName.split("_")[jobName.split("_").length - 1];
        String application_replicas = msg.getString("applications");
        if (StringUtil.isEmpty(application_replicas)) {
            return UnitResponse.createUnknownError(null, "Nothing changed.");
        }
        try {
            return UnitResponse.createSuccess(DeploymentUtil.replicate(application_replicas, env));
        } catch (DeploymentUtil.CloudApiFailedException e) {
            return UnitResponse.createException(e, "调用容器API失败");
        } catch (IllegalArgumentException e) {
            return UnitResponse.createException(e);
        }
    }

}
