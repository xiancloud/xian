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

import java.util.ArrayList;
import java.util.List;

import static info.xiancloud.qclouddocker.api.unit.custom.DeploymentUtil.*;

/**
 * @author happyyangyuan
 */
public class RollbackApplications implements Unit {
    @Override
    public String getName() {
        return "rollbackApplications";
    }

    @Override
    public Group getGroup() {
        return QcloudContainerGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("将application回滚到指定的版本")
                .setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("applications", String.class,
                        "应用名列表，以逗号分隔，如果为空表示回滚当前构建项目对应的所有现有服务；" +
                                "可以单独为application指定回退的历史版本号，以等号分隔：" +
                                "eg. application01=45,application02=46  " +
                                "或 " +
                                "eg. application01=45,application02", NOT_REQUIRED)
                .add("jobName", String.class, "jenkins项目名，项目名的标准格式为：git库名称_环境名,eg. xian_plugin_production。", REQUIRED)
                /*.add("historyBuildNumber", String.class, "你需要回滚至哪个版本，如果为空，表示回滚至上一个版本", NOT_REQUIRED)*/;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String jobName = msg.getString("jobName");
        List<String> applicationsToRollback = applicationsWithOrWithoutHistVersion(msg.getString("applications"), jobName);
        for (String applicationWithOrWithoutRollbackVersion : applicationsToRollback) {
            String historyBuildNumber = null;
            if (applicationWithOrWithoutRollbackVersion.contains("=")) {
                historyBuildNumber = applicationWithOrWithoutRollbackVersion.split("=")[1].trim();
            }
            if (StringUtil.isEmpty(historyBuildNumber)) {
                LOG.info("未给定回退版本，那么默认回滚至上一个版本");
                rollbackService(jobName, applicationWithOrWithoutRollbackVersion);
            } else {
                updateService(historyBuildNumber, jobName, applicationWithOrWithoutRollbackVersion.split("=")[0].trim()/*applicationWithOrWithoutRollbackVersion*/);
            }
        }
        return UnitResponse.createSuccess(new JSONObject() {{
            put("rollbackedApplications", applicationsToRollback);
        }});
    }

    //获取需要回滚的application列表
    private static List<String> applicationsWithOrWithoutHistVersion(String applicationsStr, String jobName) {
        if (StringUtil.isEmpty(applicationsStr))
            return runningServices(jobName);
        List<String> applications = new ArrayList<>();
        for (String application : applicationsStr.split(",")) {
            if (!StringUtil.isEmpty(application.trim()))
                applications.add(application.trim());
        }
        return applications;
    }

}
