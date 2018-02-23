package info.xiancloud.yy.qcloud_docker_service;

import info.xiancloud.qclouddocker.api.App;

/**
 * @author happyyangyuan
 */
public class QcloudDockerServiceTest {

    public static void main(String[] args) throws Exception {

        // -----服务实例相关接口
        // describeServiceInstance();
        // modifyServiceReplicas();
        // deleteInstances();

        // -------服务相关接口
        // createClusterService();
        // describeClusterService();
        // describeClusterServiceInfo();
        // modifyClusterService();
        // modifyServiceDescription();
        // describeServiceEvent();
        // pauseClusterService();
        // resumeClusterService();
        // rollBackClusterService();
        App.modifyClusterServiceImage();
        // deleteClusterService();
    }
}
