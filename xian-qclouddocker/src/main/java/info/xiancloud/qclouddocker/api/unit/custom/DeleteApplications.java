package info.xiancloud.qclouddocker.api.unit.custom;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.StringUtil;
import info.xiancloud.qclouddocker.api.service.QcloudContainerGroup;

import java.util.List;

import static info.xiancloud.qclouddocker.api.unit.custom.DeploymentUtil.runningServices;

/**
 * @author happyyangyuan
 */
public class DeleteApplications implements Unit {
    @Override
    public String getName() {
        return "deleteApplications";
    }

    @Override
    public Group getGroup() {
        return QcloudContainerGroup.singleton;
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("jobName", String.class, "jenkins项目名，传入_${env}表示删除环境下所有服务", REQUIRED)
                ;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String jobName = msg.get("jobName", String.class),
                env = jobName.split("_")[jobName.split("_").length - 1],
                clusterId = DeploymentUtil.clusterId(env),
                repo = jobName.substring(0, jobName.lastIndexOf('_'));
        List<String> servicesToDelete;
        if (StringUtil.isEmpty(repo)) {
            servicesToDelete = DeploymentUtil.envRunningService(env);
        } else {
            servicesToDelete = runningServices(jobName);
        }
        for (String runningService : servicesToDelete) {
            DeploymentUtil.deleteService(clusterId, runningService, env);
        }
        return UnitResponse.success(new JSONObject() {{
            put("servicesToDelete", servicesToDelete);
        }});
    }

    public static void main(String[] args) {
        deleteEnv("dev");
    }

    private static void deleteEnv(String env) {
        String[] gits = new String[]{"xian", "paymembers", "xian_basic_module", "xian_module", "xian_plugin", "stat_center"};
        for (String git : gits) {
            String jobName = git + "_" + env;
            System.out.println(JSON.toJSONString(SyncXian.call(DeleteApplications.class, new JSONObject() {{
                put("jobName", jobName);
            }}).toJSONObject(), true));
        }
    }
}
