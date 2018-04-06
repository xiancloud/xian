package info.xiancloud.qclouddocker.api.unit.custom;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.util.LOG;
import info.xiancloud.qclouddocker.api.service.QcloudContainerGroup;

import java.util.List;

/**
 * @author happyyangyuan
 */
public class RefreshApplications implements Unit {
    @Override
    public String getName() {
        return "refreshApplications";
    }

    @Override
    public Group getGroup() {
        return QcloudContainerGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("更新指定jenkinsJob下所有服务：删除废弃服务，更新已存服务，新建不存在的服务");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("applications", String.class, "应用名列表，以逗号分隔，不允许为空；" +
                        "注意，这个参数并不是对应的Jenkins构建项目的那个applications入参，" +
                        "而是由refreshApplications脚本扫描xian_runtime包得到的入参。", REQUIRED)
                .add("jobName", String.class, "jenkins项目名", REQUIRED)
                .add("buildNumber", String.class, "Jenkins构建编号", REQUIRED)
                ;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String jobName = msg.get("jobName", String.class),
                buildNumber = msg.get("buildNumber", String.class),
                env = jobName.split("_")[jobName.split("_").length - 1],
                clusterId = "production".equals(env) ? DeploymentUtil.P_CLUSTER_ID : DeploymentUtil.NP_CLUSTER_ID;
        LOG.info("发布参数：jobName=" + jobName + ",env=" + env + ",clusterId=" + clusterId);
        List<String> runningServices = DeploymentUtil.runningServices(jobName);
        List<String> applicationList = DeploymentUtil.parseApplicationString(msg.get("applications", String.class));
        List<String> servicesToRemove = DeploymentUtil.servicesToRemove(applicationList, runningServices, env);
        for (String serviceToRemove : servicesToRemove) {
            DeploymentUtil.deleteService(clusterId, serviceToRemove, env);
        }
        String newImage = DeploymentUtil.REGISTRY_URI() + jobName + ":" + buildNumber;
        List<String> envRunningServiceList = DeploymentUtil.envRunningService(env);
        List<String> servicesToUpdate = DeploymentUtil.servicesToUpdate(applicationList, /*runningServices 现改为支持交叉更新服务，从而可以支持拆分git的场景*/envRunningServiceList, env);
        for (String serviceToUpdate : servicesToUpdate) {
            DeploymentUtil.updateService(newImage, clusterId, serviceToUpdate, env);
        }
        List<String> applicationsToCreate = DeploymentUtil.applicationsToCreate(applicationList, /*runningServices 同上*/envRunningServiceList, env);
        for (String application : applicationsToCreate) {
            DeploymentUtil.createService(newImage, env, application, clusterId);
        }
        return UnitResponse.createSuccess(new JSONObject() {{
            put("runningServices", runningServices);
            put("applicationsInXianRuntime", applicationList);
            put("servicesToRemove", servicesToRemove);
            put("servicesToUpdate", servicesToUpdate);
            put("applicationsToCreate", applicationsToCreate);
            put("newImage", newImage);
        }}).setContext(UnitResponse.Context.create().setPretty(true));
    }

    public static void main(String[] args) {
        Xian.call(RefreshApplications.class, new JSONObject() {{
            put("applications", "communication");
            put("jobName", "xian_predev");
            put("buildNumber", "193");
        }}, new NotifyHandler() {
            @Override
            protected void handle(UnitResponse unitResponse) {
                System.out.println(unitResponse);
            }
        });
    }
}
