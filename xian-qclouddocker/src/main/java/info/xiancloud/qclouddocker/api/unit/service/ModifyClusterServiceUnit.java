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
 * 修改服务
 *
 * @author yyq
 */
public class ModifyClusterServiceUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "modifyClusterService";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("修改服务");
    }

    @Override
    public Input getInput() {
        return new Input().add("clusterId", String.class, "集群ID，可通过查询集群接口反回字段中的 clusterId获取", REQUIRED)
                .add("serviceName", String.class, "服务名,首字符必须为小写字母，尾字符必须是小写字母和数字，中间是横杠或者数字或者小写字母", REQUIRED)
                .add("serviceDesc", String.class, "服务描述")
                .add("replicas", int.class, "实例副本数", REQUIRED)
                .add("strategy", String.class,
                        "服务更新策略，Recreate或者RollingUpdate。Recreate方式会在更新服务之前，kill掉该服务下所有的容器，然后根据新的参数重新创建容器。RollingUpdate方式会对容器进行滚动升级，先kill掉部分容器，再根据新的参数创建部分新的容器，当新的容器启动ok后，重复kill老的容器和创建新的容器，直至所有新的容器都创建成功，老的容器kill完，实现一个灰度发布的过程。",
                        REQUIRED)
                .add("minReadySeconds", int.class,
                        "单位秒。滚动升级时，部分新的容器启动后，接着启动新的容器的等待时间，例如minReadySeconds设置成10，集群会先启动1个新的容器，如果新的容器起来了，将会等待10s后，再启动1个新的容器，直到新容器的数目达到replicas的数目"
                )
                .add("accessType", String.class,
                        "服务访问方式 LoadBalancer：方式会为服务创建一个外网负载均衡，访问该负载均衡的ip、端口时，会把流量转发到该服务。NodePort：会在集群内每个Node上开启一个端口，通过访问任意个Node的ip和开启的端口，会把流量转发到该服务。SvcLBTypeInner：会创建一个内网负载均衡，需要指定subnetId,会占用该子网下的一个IP。ClusterIP：该服务不提供集群外访问，只供集群内其它服务访问，默认为ClusterIP")
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
                                    JSONObject obj = jsonArr.getJSONObject(i);
                                    params.put(prefix + "containerName", obj.getString("containerName"));
                                    params.put(prefix + "image", obj.getString("image"));
                                    //TODO
                                    //暂时忽略的参数  envs.n ， volumeMounts.n， healthCheck.n，arguments.n
                                    if (!StringUtil.isEmpty(obj.getString("cpu"))) {
                                        params.put(prefix + "cpu", obj.getString("cpu"));
                                    }
                                    if (!StringUtil.isEmpty(obj.getString("cpuLimits"))) {
                                        params.put(prefix + "cpuLimits", obj.getString("cpuLimits"));
                                    }
                                    if (!StringUtil.isEmpty(obj.getString("memory"))) {
                                        params.put(prefix + "memory", obj.getString("memory"));
                                    }
                                    if (!StringUtil.isEmpty(obj.getString("command"))) {
                                        params.put(prefix + "command", obj.getString("command"));
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
        return "ModifyClusterService";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
