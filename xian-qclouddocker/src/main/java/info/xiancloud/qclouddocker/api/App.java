package info.xiancloud.qclouddocker.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.http.HttpKit;
import info.xiancloud.qclouddocker.api.unit.custom.DeploymentUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试类
 *
 * @author yyq
 */
public class App {

    public static void main(String[] args) throws Exception {

        // -----服务实例相关接口
        // describeServiceInstance();
        // modifyServiceReplicas();
        // deleteInstances();

        // -------服务相关接口
        // createClusterService();
        //describeClusterService();
        describeClusterServiceInfo();
        // modifyClusterService();
        // modifyServiceDescription();
        // describeServiceEvent();
        // pauseClusterService();|
        // resumeClusterService();
        // rollBackClusterService();
        // modifyClusterServiceImage();
        // deleteClusterService();
    }

    // ---------服务实例相关接口

    /**
     * 删除服务实例
     */
    static void deleteInstances() { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");

        // 服务实例列表数组
        JSONArray instances = new JSONArray();
        instances.add("my-mongodb-3632199888-dwpeo");
        // instances.add("my-mongodb-3632199888-jjoqx");

        params.put("instances.n", instances.toJSONString());

        UnitResponse result = SyncXian.call("qcloudContainerService", "deleteInstances", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 修改实例服务副本数
     */
    static void modifyServiceReplicas() { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        params.put("scaleTo", 5);
        UnitResponse result = SyncXian.call("qcloudContainerService", "modifyServiceReplicas", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 查询服务实例列表
     */
    static void describeServiceInstance() { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        UnitResponse result = SyncXian.call("qcloudContainerService", "describeServiceInstance", params);
        System.out.println(result.getData().toString());
    }

    // ----------服务相关接口

    /**
     * 删除服务
     */
    static void deleteClusterService() { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        UnitResponse result = SyncXian.call("qcloudContainerService", "deleteClusterService", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 修改服务镜像
     */
    public static void modifyClusterServiceImage() throws Exception { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "communication");

        // 实例下只有一个容器 直接传 image参数即可
        params.put("image", DeploymentUtil.REGISTRY_URI() + "xian_predev:jenkins-xian_predev-400");

        // 测试多多容器修改镜像
        /*JSONArray containers = new JSONArray();
        JSONObject container = new JSONObject();
		container.put("containerName", "mongodb");
		container.put("image", "nginx");
		containers.add(container);

		params.put("containers.n", containers.toJSONString());

		UnitResponse result = Xian.call("qcloudContainerService", "modifyClusterServiceImage", params);*/
        String outString = HttpKit.post("http://localhost:9125/v1.0/qcloudContainerService/modifyClusterServiceImage")
                .addParams(params)
                .execute();
        UnitResponse result = UnitResponse.create(JSON.parseObject(outString));
        System.out.println(result.getData().toString());
    }

    /**
     * 回滚服务
     */
    static void rollBackClusterService() { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        UnitResponse result = SyncXian.call("qcloudContainerService", "rollBackClusterService", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 继续服务更新
     */
    static void resumeClusterService() { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        UnitResponse result = SyncXian.call("qcloudContainerService", "resumeClusterService", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 暂停服务更新
     */
    static void pauseClusterService() { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        UnitResponse result = SyncXian.call("qcloudContainerService", "pauseClusterService", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 获取服务事件列表
     */
    static void describeServiceEvent() { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        UnitResponse result = SyncXian.call("qcloudContainerService", "describeServiceEvent", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 修改服务描述
     */
    static void modifyServiceDescription() { // description
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        params.put("description", "嗯，修改服务描述了");
        UnitResponse result = SyncXian.call("qcloudContainerService", "modifyServiceDescription", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 修改服务
     */
    static void modifyClusterService() {
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        params.put("serviceDesc", "serverdecModify");
        params.put("strategy", "Recreate");
        // params.put("namespace", "xian");
        params.put("replicas", 1);
        // params.put("accessType","LoadBalancer");

        // 端口映射相关
        JSONArray portMappings = new JSONArray();
        JSONObject portMap = new JSONObject();
        portMap.put("lbPort", 80);
        portMap.put("containerPort", 80);
        // accessType=LoadBalancer 时，不需要传
        // portMap.put("nodePort", 36001);
        portMap.put("protocol", "TCP");
        portMappings.add(portMap);

        // ---容器相关-----
        JSONArray containers = new JSONArray();
        JSONObject objContain = new JSONObject();
        objContain.put("containerName", "mongodb");//
        objContain.put("image", "mongo");
        containers.add(objContain);

        params.put("portMappings.n", portMappings.toJSONString());
        params.put("containers.n", containers.toJSONString());

        UnitResponse result = SyncXian.call("qcloudContainerService", "modifyClusterService", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 查询服务详情
     */
    static void describeClusterServiceInfo() {
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "xiandb-dev");
        // params.put("namespace", "default");
        UnitResponse result = SyncXian.call("qcloudContainerService", "describeClusterServiceInfo", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 查询服务列表
     */
    static void describeClusterService() {
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("namespace", "predev");
        // 展示所有命名空间下的服务
        //params.put("allnamespace", 1);
        UnitResponse result = SyncXian.call("qcloudContainerService", "describeClusterService", params);
        System.out.println(result.getData().toString());
    }

    /**
     * 创建服务
     */
    static void createClusterService() {
        // Man.serial();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clusterId", "cls-768pakpq");
        params.put("serviceName", "my-mongodb");
        params.put("serviceDesc", "serverdec");
        // params.put("namespace", "xian");
        params.put("replicas", 2);
        // params.put("accessType","LoadBalancer");

        // 端口映射相关
        JSONArray portMappings = new JSONArray();
        JSONObject portMap = new JSONObject();
        portMap.put("lbPort", 80);
        portMap.put("containerPort", 80);
        // accessType=LoadBalancer 时，不需要传
        // portMap.put("nodePort", 36001);
        portMap.put("protocol", "TCP");
        portMappings.add(portMap);

        // ---容器相关-----
        JSONArray containers = new JSONArray();
        JSONObject objContain = new JSONObject();
        objContain.put("containerName", "mongodb");//
        objContain.put("image", "mongo");
        containers.add(objContain);

        params.put("portMappings.n", portMappings.toJSONString());
        params.put("containers.n", containers.toJSONString());

        UnitResponse result = SyncXian.call("qcloudContainerService", "createClusterService", params);
        System.out.println(result.getData().toString());
    }

}
