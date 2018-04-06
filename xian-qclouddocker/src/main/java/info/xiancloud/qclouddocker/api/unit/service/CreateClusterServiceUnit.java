package info.xiancloud.qclouddocker.api.unit.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;

import java.util.List;
import java.util.TreeMap;

/**
 * 创建服务
 *
 * @author yyq
 */
public class CreateClusterServiceUnit extends QCloudBaseUnit {

    @Override
    public String getName() {
        return "createClusterService";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("创建服务");
    }

    @Override
    public Input getInput() {
        return new Input().add("clusterId", String.class, "集群ID，可通过查询集群接口反回字段中的 clusterId获取", REQUIRED)
                .add("serviceName", String.class, "服务名,首字符必须为小写字母，尾字符必须是小写字母和数字，中间是横杠或者数字或者小写字母", REQUIRED)
                .add("serviceDesc", String.class, "服务描述")
                .add("replicas", int.class, "实例副本数", REQUIRED)
                .add("accessType", String.class,
                        "服务访问方式 " +
                                "LoadBalancer：方式会为服务创建一个外网负载均衡，访问该负载均衡的ip、端口时，会把流量转发到该服务。" +
                                "NodePort：会在集群内每个Node上开启一个端口，通过访问任意个Node的ip和开启的端口，会把流量转发到该服务。" +
                                "SvcLBTypeInner：会创建一个内网负载均衡，需要指定subnetId,会占用该子网下的一个IP。" +
                                "ClusterIP：该服务不提供集群外访问，只供集群内其它服务访问，默认为ClusterIP。" +
                                "None：不提供网络访问，这种方式不用传portMappings.n。")
                .add("portMappings.n", String[].class, "端口映射信息，如果容器需要提供网络访问，需要填写，否则不需要填写")
                .add("volumes.n", String[].class, "容器卷定义")
                .add("labels.n", String[].class, "服务的标签")
                .add("containers.n", String[].class, "容器数组，一个服务必须定义一个或多个容器，服务创建时会启动定义的容器", REQUIRED)
                .add("namespace", String.class, "命名空间,默认为default")
                .add("subnetId", String.class, "子网ID，请填写查询子网列表接口中返回的unSubnetId(子网统一ID)字段，accessType为SvcLBTypeInner必传");
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return super.execute(msg);
    }

    @Override
    protected void fillUnitArgs(UnitRequest msg, TreeMap<String, String> params) {

        // 添加接口请求参数
        List<Input.Obj> argList = this.getInput().getList();
        if (argList != null && !argList.isEmpty()) {
            argList.forEach(arg -> {
                if (!StringUtil.isEmpty(msg.getArgMap().get(arg.getName()))) {
                    // 基本类型
                    if (arg.getClazz().isPrimitive() || arg.getClazz() == String.class) {
                        params.put(arg.getName(), msg.getArgMap().get(arg.getName()) + "");
                    } else {
                        // 复合类型参数
                        JSONArray jsonArr = JSON.parseArray(msg.getArgMap().get(arg.getName()).toString());
                        switch (arg.getName()) {
                            case "portMappings.n":
                                for (int i = 0; i < jsonArr.size(); i++) {
                                    // 参数前缀
                                    String prefix = String.format("portMappings.%s.", i);
                                    JSONObject obj = jsonArr.getJSONObject(i);
                                    params.put(prefix + "lbPort", obj.getString("lbPort"));
                                    params.put(prefix + "containerPort", obj.getString("containerPort"));
                                    if (!StringUtil.isEmpty(obj.getString("nodePort"))) {
                                        params.put(prefix + "nodePort", obj.getInteger("nodePort").toString());
                                    }
                                    params.put(prefix + "protocol", obj.getString("protocol"));
                                }
                                break;
                            case "volumes.n":
                                for (int i = 0; i < jsonArr.size(); i++) {
                                    String prefix = String.format("volumes.%s.", i);
                                    JSONObject obj = jsonArr.getJSONObject(i);
                                    params.put(prefix + "name", obj.getString("name"));
                                    params.put(prefix + "hostPath", obj.getString("hostPath"));
                                    if (!StringUtil.isEmpty(obj.getString("hostPath"))) {
                                        params.put(prefix + "hostPath", obj.getString("hostPath"));
                                    }
                                    if (!StringUtil.isEmpty(obj.getString("cbsDiskId"))) {
                                        params.put(prefix + "cbsDiskId", obj.getString("cbsDiskId"));
                                    }
                                }
                                break;
                            case "labels.n":
                                for (int i = 0; i < jsonArr.size(); i++) {
                                    String prefix = String.format("labels.%s.", i);
                                    JSONObject obj = jsonArr.getJSONObject(i);
                                    params.put(prefix + "key", obj.getString("key"));
                                    params.put(prefix + "value", obj.getString("value"));
                                }
                                break;
                            case "containers.n":
                                for (int i = 0; i < jsonArr.size(); i++) {
                                    String prefix = String.format("containers.%s.", i);
                                    JSONObject containerObj = jsonArr.getJSONObject(i);
                                    params.put(prefix + "containerName", containerObj.getString("containerName"));
                                    params.put(prefix + "image", containerObj.getString("image"));
                                    //TODO
                                    //暂时忽略的参数volumeMounts.n，arguments.n
                                    JSONArray healthChecks = containerObj.getJSONArray("healthCheck.n");
                                    if (healthChecks != null && !healthChecks.isEmpty()) {
                                        for (int j = 0; j < healthChecks.size(); j++) {
                                            if (healthChecks.getJSONObject(j).getString("type") == null)
                                                throw new IllegalArgumentException("入参" + prefix + "healthCheck." + j + ".type" + "不允许为空");
                                            params.put(prefix + "healthCheck." + j + ".type", healthChecks.getJSONObject(j).getString("type"));
                                            params.put(prefix + "healthCheck." + j + ".healthNum", healthChecks.getJSONObject(j).getOrDefault("healthNum", 1) + "");
                                            params.put(prefix + "healthCheck." + j + ".unhealthNum", healthChecks.getJSONObject(j).getOrDefault("unhealthNum", 3) + "");
                                            params.put(prefix + "healthCheck." + j + ".intervalTime", healthChecks.getJSONObject(j).getOrDefault("intervalTime", 3) + "");
                                            params.put(prefix + "healthCheck." + j + ".timeOut", healthChecks.getJSONObject(j).getOrDefault("timeOut", 2) + "");
                                            params.put(prefix + "healthCheck." + j + ".delayTime", healthChecks.getJSONObject(j).getOrDefault("delayTime", 0) + "");
                                            if (healthChecks.getJSONObject(j).getString("checkMethod") == null)
                                                throw new IllegalArgumentException("入参" + prefix + "healthCheck." + j + ".checkMethod" + "不允许为空");
                                            params.put(prefix + "healthCheck." + j + ".checkMethod", healthChecks.getJSONObject(j).getString("checkMethod"));
                                            if (healthChecks.getJSONObject(j).getString("port") != null)
                                                params.put(prefix + "healthCheck." + j + ".port", healthChecks.getJSONObject(j).getString("port"));
                                            if (healthChecks.getJSONObject(j).getString("protocol") != null)
                                                params.put(prefix + "healthCheck." + j + ".protocol", healthChecks.getJSONObject(j).getString("protocol"));
                                            if (healthChecks.getJSONObject(j).getString("path") != null)
                                                params.put(prefix + "healthCheck." + j + ".path", healthChecks.getJSONObject(j).getString("path"));
                                            if (healthChecks.getJSONObject(j).getString("cmd") != null)
                                                params.put(prefix + "healthCheck." + j + ".cmd", healthChecks.getJSONObject(j).getString("cmd"));
                                        }
                                    }
                                    JSONArray envs = containerObj.getJSONArray("envs.n");
                                    if (envs != null && !envs.isEmpty()) {
                                        for (int j = 0; j < envs.size(); j++) {
                                            params.put(prefix + "envs." + j + ".name", envs.getJSONObject(j).getString("name"));
                                            params.put(prefix + "envs." + j + ".value", envs.getJSONObject(j).getString("value"));
                                        }
                                    }
                                    if (!StringUtil.isEmpty(containerObj.getString("workingDir"))) {
                                        params.put(prefix + "workingDir", containerObj.getString("workingDir"));
                                    }
                                    if (!StringUtil.isEmpty(containerObj.getString("cpu"))) {
                                        params.put(prefix + "cpu", containerObj.getString("cpu"));
                                    }
                                    if (!StringUtil.isEmpty(containerObj.getString("cpuLimits"))) {
                                        params.put(prefix + "cpuLimits", containerObj.getString("cpuLimits"));
                                    }
                                    if (!StringUtil.isEmpty(containerObj.getString("memory"))) {
                                        params.put(prefix + "memory", containerObj.getString("memory"));
                                    }
                                    if (!StringUtil.isEmpty(containerObj.getString("command"))) {
                                        params.put(prefix + "command", containerObj.getString("command"));
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }
    }

    @Override
    public String getAction() {
        return "CreateClusterService";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
