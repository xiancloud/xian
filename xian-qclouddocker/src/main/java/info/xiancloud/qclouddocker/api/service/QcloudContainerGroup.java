package info.xiancloud.qclouddocker.api.service;

import info.xiancloud.core.socket.ISocketGroup;

/**
 * 腾讯云容器
 *
 * @author yyq
 */
public class QcloudContainerGroup implements ISocketGroup {

    @Override
    public String getName() {
        return "qcloudContainerService";
    }

    @Override
    public String getDescription() {
        return "腾讯云容器服务";
    }

    public static final QcloudContainerGroup singleton = new QcloudContainerGroup();
}
