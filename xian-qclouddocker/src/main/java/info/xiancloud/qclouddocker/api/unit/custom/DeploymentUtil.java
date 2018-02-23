package info.xiancloud.qclouddocker.api.unit.custom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.*;
import info.xiancloud.qclouddocker.api.unit.service.*;
import info.xiancloud.qclouddocker.api.unit.serviceinstance.ModifyServiceReplicasUnit;

import java.util.*;

/**
 * Helper class for qcloud container group.
 *
 * @author happyyangyuan
 */
public class DeploymentUtil {
    static final String NP_CLUSTER_ID = EnvConfig.get("dockerServiceNonproductionClusterId"),
            P_CLUSTER_ID = EnvConfig.get("dockerServiceProductionClusterId");

    //每次均从配置文件加载，实现动态更新配置的功能
    public static String REGISTRY_URI() {
        return EnvConfig.get("dockerServiceRegistryUrl");
    }

    static String clusterId(String env) {
        return "production".equals(env) ? P_CLUSTER_ID : NP_CLUSTER_ID;
    }

    static String clusterIdByJobName(String jobName) {
        return clusterId(envByJobName(jobName));
    }

    static String namespaceByJobName(String jobName) {
        return namespaceByEnv(envByJobName(jobName));
    }

    static String namespaceByEnv(String env) {
        return env;
    }

    static String image(String jobName, String buildNumber) {
        return REGISTRY_URI() + jobName + ":" + buildNumber;
    }

    static String imageVersion(String image) {
        int length = image.split(":").length;
        if (length >= 2) {
            return image.split(":")[length - 1];
        } else {
            return "latest";
        }
    }

    static String imageSimpleName(String image) {
        String withoutVersion;
        if (image.contains(":")) {
            withoutVersion = image.split(":")[image.split(":").length - 2];
        } else {
            withoutVersion = image;
        }
        return withoutVersion.split("/")[withoutVersion.split("/").length - 1];
    }

    /**
     * @return jobName对应的运行中的服务名列表e
     * @deprecated 1、有一些性能问题，它遍历namespace下所有服务，每个执行describeService接口调用；
     * 2、其实没有需求必须要列出某个镜像下所有服务的，请直接使用 {@link #envRunningService(String)}
     */
    static List<String> runningServices(String jobName) {
        return runningServices(clusterId(envByJobName(jobName)), jobName);
    }

    /**
     * @param clusterId clusterId
     * @param jobName   Jenkins构建项目名
     * @return 获取到jobName对应的运行中的服务名列表
     * @deprecated 即将改为私有方法，禁止外部调用，请使用{@link #runningServices(String)}替代
     */
    private static List<String> runningServices(String clusterId, String jobName) {
        List<String> runningServices = new ArrayList<>();
        for (String serviceName : allServicesInCluster(clusterId, envByJobName(jobName))) {
            String imageName = imageSimpleName(image(serviceName, clusterId, envByJobName(jobName)));
            if (Objects.equals(imageName, jobName)) {
                runningServices.add(serviceName);
            }
        }
        LOG.info("runningServices=" + runningServices);
        return runningServices;
    }

    /**
     * @return 环境下运行的所有服务列表
     */
    static List<String> envRunningService(String env) {
        return envRunningService(clusterId(env), env);
    }

    /**
     * 已改为私有方法，禁止外部调用
     */
    private static List<String> envRunningService(String clusterId, String env) {
        List<String> runningServices = new ArrayList<>();
        for (String serviceName : allServicesInCluster(clusterId, env)) {
            String serviceEnv = serviceName.split("-")[serviceName.split("-").length - 1];
            if (Objects.equals(serviceEnv, env)) {
                runningServices.add(serviceName);
            }
        }
        LOG.info("envRunningServices=" + runningServices);
        return runningServices;
    }

    /**
     * @param clusterId 集群id
     * @param namespace 命名空间，如果为空则查询所有空间
     * @deprecated 即将改为私有方法，禁止外部调用
     */
    static List<String> allServicesInCluster(String clusterId, String namespace) {
        List<String> serviceNames = new ArrayList<>();
        JSONArray services = SyncXian.call(DescribeClusterServiceUnit.class,
                new JSONObject() {{
                    put("clusterId", clusterId);
                    if (StringUtil.isEmpty(namespace))
                        put("allnamespace", 1);
                    else
                        put("namespace", namespace);
                }}
        ).dataToJson().getJSONObject("data").getJSONArray("services");
        for (int i = 0; i < services.size(); i++) {
            JSONObject service = services.getJSONObject(i);
            serviceNames.add(service.getString("serviceName"));
        }
        LOG.info("集群内所有服务列表：" + serviceNames);
        return serviceNames;
    }

    /**
     * 获取需要更新的application列表，实现逻辑为：入参applicationsToDeploy与正在运行的服务取交集；
     * 为了通用性，这里并没有使用namespace来区分环境，而是解析服务名得到服务是哪个环境的
     *
     * @param applicationsToDeploy 需要发布的application列表，如过传空，那么返回空列表
     * @param runningServices      集群中正在运行中的服务列表
     * @param env                  环境名,eg. dev、test、production
     * @return 需要更新的applications
     */
    static List<String> applicationsToUpdate(List<String> applicationsToDeploy, List<String> runningServices, String env) {
        List<String> applicationsToUpdate = new ArrayList<>();
        if (applicationsToDeploy != null && !applicationsToDeploy.isEmpty()) {
            for (String application : applicationsToDeploy) {
                if (runningServices.contains(serviceName(application, env))) {
                    applicationsToUpdate.add(application);
                }
            }
        } else {
            LOG.info("传入了空的待发布application列表，因此返回空列表");
        }
        LOG.info("applicationsToUpdate=" + applicationsToUpdate);
        return applicationsToUpdate;
    }

    /**
     * 要发布的服务和运行的服务取交集；
     * 注意：如果传入的参数applicationToDeploy为空，那么将会返回集群内所有服务列表
     *
     * @param applicationsToDeploy 你要发布的application列表
     * @param runningServices      正在运行的服务列表
     * @deprecated 即将修改为private仅供内部使用；请使用{@link #applicationsToUpdate(List, List, String)}替代
     */
    static List<String> servicesToUpdate(List<String> applicationsToDeploy, List<String> runningServices, String env) {
        List<String> servicesToUpdate = new ArrayList<>();
        if (applicationsToDeploy != null && !applicationsToDeploy.isEmpty()) {
            for (String application : applicationsToDeploy) {
                String application2ServiceName = serviceName(application, env);
                if (runningServices.contains(application2ServiceName)) {
                    servicesToUpdate.add(application2ServiceName);
                }
            }
        } else {
            servicesToUpdate.addAll(runningServices);
        }
        LOG.info("servicesToUpdate=" + servicesToUpdate);
        return servicesToUpdate;
    }

    //获取需要新建的服务列表
    static List<String> applicationsToCreate(List<String> applicationsToDeploy, List<String> runningServices, String env) {
        List<String> applicationsToCreate = new ArrayList<>();
        if (applicationsToDeploy != null && !applicationsToDeploy.isEmpty()) {
            for (String application : applicationsToDeploy) {
                String application2ServiceName = serviceName(application, env);
                if (!runningServices.contains(application2ServiceName)) {
                    applicationsToCreate.add(application);
                }
            }
        }
        LOG.info("applicationsToCreate=" + applicationsToCreate);
        return applicationsToCreate;
    }

    //获取要废弃的服务列表
    static List<String> servicesToRemove(List<String> applicationsInXianRuntime, List<String> runningServices, String env) {
        List<String> servicesToRemove = new ArrayList<>();
        if (applicationsInXianRuntime != null && !applicationsInXianRuntime.isEmpty()) {
            List<String> servicesToDeploy = new ArrayList<String>() {{
                for (String application : applicationsInXianRuntime) {
                    add(serviceName(application, env));
                }
            }};
            if (runningServices != null && !runningServices.isEmpty()) {
                for (String runningService : runningServices) {
                    if (!servicesToDeploy.contains(runningService)) {
                        servicesToRemove.add(runningService);
                    }
                }
            }
        } else {
            LOG.info("如果传入空的application列表，那么特殊对待，不删除现有任何服务");
        }
        LOG.info("servicesToRemove=" + servicesToRemove);
        return servicesToRemove;
    }

    /**
     * 解析applications字符串得到列表
     *
     * @param applications 以逗号分隔的application列表字符串
     * @return 列表
     * @deprecated 其实只是解析一下字符串而已，这个方法名取得不合适；即将删除。
     */
    private static List<String> applicationsToDeploy(String applications) {
        return parseApplicationString(applications);
    }

    /**
     * 其实只是解析一下字符串而已
     *
     * @param applications 以逗号分隔的application列表字符串
     * @return 列表
     */
    static List<String> parseApplicationString(String applications) {
        List<String> applicationsList = new ArrayList<>();
        if (applications != null && !applications.isEmpty()) {
            String[] applicationArray = applications.split(",");
            for (String application : applicationArray) {
                if (!StringUtil.isEmpty(application))
                    applicationsList.add(application.trim());
            }
        }
        return applicationsList;
    }

    /**
     * 检查application是否存在于xian_runtime包内，如果不存在那么抛出异常
     *
     * @param applications                待检查的application列表
     * @param applicationsInXianRuntime xian_runtime包内的application集合
     * @throws RuntimeException 提示application不存在
     */
    static void checkApplicationsExits(List<String> applications, List<String> applicationsInXianRuntime) {
        List<String> nonExitsApplications = ArrayUtil.getNonIntersectionInListA(applications, applicationsInXianRuntime);
        if (!nonExitsApplications.isEmpty()) {
            throw new RuntimeException("application不存在：" + nonExitsApplications);
        }
    }

    /**
     * 根据jenkins项目名称得到环境名
     */
    static String envByJobName(String jobName) {
        if (StringUtil.isEmpty(jobName))
            throw new IllegalArgumentException("jobName不允许为空");
        return jobName.substring(jobName.lastIndexOf("_") + 1);
    }

    //application to serviceName
    static String serviceName(String application, String env) {
        LOG.debug("服务名是小写和中划线，并且带了环境后缀的");
        return application.toLowerCase().replace("_", "-") + "-" + env;
    }

    /**
     * @deprecated 即将改为私有方法，禁止外部调用
     */
    //查询服务对应的image
    private static String image(String service, String clusterId, String namespace) {
        if (StringUtil.isEmpty(namespace))
            throw new IllegalArgumentException("namespace不允许为空");
        JSONObject container = SyncXian.call(DescribeClusterServiceInfoUnit.class, new JSONObject() {{
            put("clusterId", clusterId);
            put("serviceName", service);
            put("namespace", namespace);
        }}).dataToJson().getJSONObject("data").getJSONObject("group").getJSONArray("containers").getJSONObject(0);
        return container.getString("image");
    }

    /**
     * 更接近近业务场景的创建服务方法
     */
    static String createService(String jobName, String application, String buildNum) {
        String image = image(jobName, buildNum);
        createService(image, envByJobName(jobName), application, clusterId(envByJobName(jobName)));
        return image;
    }

    /**
     * @param fullImageName
     * @param env
     * @param application
     * @param clusterId
     * @deprecated 即将改为私有方法，禁止外部调用;请使用{@link #createService(String, String, String)}替代
     */
    static void createService(String fullImageName, String env, String application, String clusterId) {
        String serviceName = application.toLowerCase().replace("_", "-") + "-" + env;
        SyncXian.call(CreateClusterServiceUnit.class, new JSONObject() {{
            put("clusterId", clusterId);
            put("serviceName", serviceName);
            put("serviceDesc", serviceName);
            put("accessType", "None");
            put("replicas", 1);
            put("namespace", env);
            JSONArray containers = new JSONArray();
            JSONObject objContain = new JSONObject();
            LOG.debug("容器名不允许包含'-'");
            objContain.put("containerName", serviceName.replace("-", ""));
            objContain.put("image", fullImageName);
            objContain.put("envs.n", new JSONArray() {{
                add(new JSONObject() {{
                    put("name", "XIAN_APPLICATION");
                    put("value", application);
                }});
                add(new JSONObject() {{
                    put("name", "XIAN_ENV");
                    put("value", env);
                }});
            }});
            objContain.put("healthCheck.n", new JSONArray() {{
                add(new JSONObject() {{
                    put("type", "readyCheck");
                    put("checkMethod", "methodCmd");
                    put("cmd", "../readyCheck.sh");
                }});
            }});
            //设置工作路径 eg./data/workspace/xian_runtime/communication
            objContain.put("workingDir", "/data/workspace/xian_runtime/" + application);
            if (!EnvUtil.PRODUCTION.endsWith(env)) {
                //设置memory限制，腾讯云容器服务内存request和limit是取的这同一个值，单位为m
                objContain.put("memory", 1024);
            }
            containers.add(objContain);
            put("containers.n", containers.toJSONString());
        }});
    }

    /**
     * @param clusterId
     * @param serviceName
     * @param namespace
     * @deprecated 即将改为私有方法，禁止外部调用
     */
    static void deleteService(String clusterId, String serviceName, String namespace) {
        if (StringUtil.isEmpty(namespace))
            throw new IllegalArgumentException("namespace不允许为空");
        Map<String, Object> params = new HashMap<>();
        params.put("clusterId", clusterId);
        params.put("serviceName", serviceName);
        params.put("namespace", namespace);
        SyncXian.call(DeleteClusterServiceUnit.class, params).throwExceptionIfNotSuccess("删服务失败:" + serviceName);
    }

    static void rollbackService(String jobName, String application) {
        SyncXian.call(RollBackClusterServiceUnit.class, new JSONObject() {{
            put("clusterId", DeploymentUtil.clusterIdByJobName(jobName));
            put("serviceName", DeploymentUtil.serviceName(application, DeploymentUtil.envByJobName(jobName)));
            put("namespace", DeploymentUtil.namespaceByJobName(jobName));
        }}).throwExceptionIfNotSuccess();
    }

    static void updateService(String buildNumber, String jobName, String application) {
        updateService(
                image(jobName, buildNumber),
                clusterIdByJobName(jobName),
                serviceName(application, envByJobName(jobName)),
                namespaceByJobName(jobName));
    }

    /**
     * @param newImage
     * @param clusterId
     * @param serviceToUpdate
     * @param namespace
     * @deprecated 即将改为私有方法，禁止外部调用；请使用{@link #updateService(String, String, String)}替代
     */
    static void updateService(String newImage, String clusterId, String serviceToUpdate, String namespace) {
        if (StringUtil.isEmpty(namespace))
            throw new IllegalArgumentException("namespace不允许为空");
        SyncXian.call(ModifyClusterServiceImageUnit.class, new JSONObject() {{
            put("clusterId", clusterId);
            put("serviceName", serviceToUpdate);
            put("image", newImage);
            put("namespace", namespace);
        }});
    }

    static void modifyReplica(String application, String env, int replica) throws CloudApiFailedException {
        UnitResponse o = SyncXian.call(ModifyServiceReplicasUnit.class, new JSONObject() {{
            put("clusterId", clusterId(env));
            put("serviceName", DeploymentUtil.serviceName(application, env));
            put("scaleTo", replica);
            put("namespace", env);
        }});
        if (!o.succeeded()) {
            String unitName = LocalUnitsManager.getUnitByUnitClass(ModifyServiceReplicasUnit.class).getName();
            throw new CloudApiFailedException(unitName);
        }
    }

    static List<Pair<String, Integer>> replicate(String application_replicas, String env) throws IllegalArgumentException, CloudApiFailedException {
        if (StringUtil.isEmpty(application_replicas))
            throw new IllegalArgumentException("错误输入：applications列表不允许为空");
        List<Pair<String, Integer>> list = new ArrayList<>();
        for (String application_replica : application_replicas.trim().split(",")) {
            if (StringUtil.isEmpty(application_replica)) {
                continue;
            }
            application_replica = application_replica.trim();
            if (!application_replica.contains("=")) {
                LOG.warn("入参必须是application=replicaInt格式：" + application_replica);
                continue;
            }
            String application = application_replica.split("=")[0].trim();
            int replica;
            try {
                replica = new Integer(application_replica.split("=")[1].trim());
            } catch (NumberFormatException badNumber) {
                throw new IllegalArgumentException("错误输入：" + application_replica + "；提示：实例数量必须>=0");
//                return UnitResponse.failureWithMsg("错误输入：" + application_replica + "；提示：实例数量必须>=0");
            }
            if (replica < 0) {
                throw new IllegalArgumentException("错误输入：" + application_replica + "；实例数量必须>=0");
//                return UnitResponse.failureWithMsg("错误输入：" + application_replica + "；实例数量必须>=0");
            }
            list.add(Pair.of(application, replica));
        }
        for (Pair<String, Integer> application_replica : list) {
            DeploymentUtil.modifyReplica(application_replica.fst, env, application_replica.snd);
        }
        return list;
    }

    public static class CloudApiFailedException extends Exception {
        private String api;

        private CloudApiFailedException(String api) {
            this.api = api;
        }

        @Override
        public String getLocalizedMessage() {
            return "请求失败：" + api;
        }
    }

}
