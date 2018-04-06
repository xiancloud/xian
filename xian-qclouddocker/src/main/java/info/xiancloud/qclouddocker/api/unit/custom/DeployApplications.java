package info.xiancloud.qclouddocker.api.unit.custom;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.qclouddocker.api.service.QcloudContainerGroup;

import java.util.List;

/**
 * 更新/新建服务指定，不删除服务，删除服务需要在腾讯云后台进行
 *
 * @author happyyangyuan
 * @deprecated 1、deployApplications不应当支持设置副本数的功能
 * 2、本类不支持检查入参applications的合法性
 * 3、请使用{@link DeployApplications_v_2 }替代
 */
public class DeployApplications implements Unit {
    @Override
    public String getName() {
        return "deployApplications";
    }

    @Override
    public Group getGroup() {
        return QcloudContainerGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("更新指定环境指定镜像下的指定服务/更新实例数量；" +
                "@deprecated 1、deployApplications不应当支持设置副本数的功能\n" +
                " * 2、本类不支持检查入参applications的合法性\n" +
                " * 3、请使用{@link DeployApplications_v_2 }替代");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("applications", String.class, "应用名列表，以逗号分隔，如果为空表示更新所有现有服务")
                .add("jobName", String.class, "jenkins项目名", REQUIRED)
                .add("buildNumber", String.class, "Jenkins构建编号", REQUIRED)
                ;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String applications = msg.getString("applications");
        UnitResponse unitResponseObject;
        if (applications != null && applications.contains("=")) {
            //todo 目前向下兼容，保留一段时间，日后deployApplications将不再支持修改副本数量
            unitResponseObject = replica(msg);
        } else
            unitResponseObject = deploy(msg);
        unitResponseObject.getContext().setPretty(true);
        return unitResponseObject;
    }

    //修改实例数量，设置为0表示停xian服务
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

    //原发布逻辑
    private UnitResponse deploy(UnitRequest msg) {
        String jobName = msg.get("jobName", String.class),
                buildNumber = msg.get("buildNumber", String.class),
                env = jobName.split("_")[jobName.split("_").length - 1],
                clusterId = DeploymentUtil.clusterId(env);
        LOG.info("发布参数：jpbName=" + jobName + ",env=" + env + ",clusterId=" + clusterId);
        List<String> envRunningServices = DeploymentUtil.envRunningService(env);
        List<String> applicationsToDeploy = DeploymentUtil.parseApplicationString(msg.get("applications", String.class));
        String newImage = DeploymentUtil.REGISTRY_URI() + jobName + ":" + buildNumber;
        List<String> servicesToUpdate = DeploymentUtil.servicesToUpdate(applicationsToDeploy, envRunningServices, env);
        for (String serviceToUpdate : servicesToUpdate) {
            DeploymentUtil.updateService(newImage, clusterId, serviceToUpdate, env);
        }
        List<String> applicationsToCreate = DeploymentUtil.applicationsToCreate(applicationsToDeploy, envRunningServices, env);
        for (String application : applicationsToCreate) {
            DeploymentUtil.createService(newImage, env, application, clusterId);
        }
        return UnitResponse.createSuccess(new JSONObject() {{
            put("newImage", newImage);
            put("runningServices", envRunningServices);
            put("applicationsToDeploy", applicationsToDeploy);
            put("servicesToUpdate", servicesToUpdate);
            put("applicationsToCreate", applicationsToCreate);
        }});
    }


}
