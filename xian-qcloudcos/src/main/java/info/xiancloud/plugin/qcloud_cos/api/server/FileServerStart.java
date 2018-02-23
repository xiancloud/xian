package info.xiancloud.plugin.qcloud_cos.api.server;

import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.init.shutdown.ShutdownHook;
import info.xiancloud.plugin.util.LOG;

public class FileServerStart implements IStartService, ShutdownHook {

    private static HttpFileServer singleton;

    @Override
    public boolean startup() {
        String portStr = EnvConfig.get("port");
        int port = 8080;
        try {
            port = Integer.parseInt(portStr);
        } catch (Exception e) {
            // 默认8080端口
        }
        try {
            singleton = new HttpFileServer().start(port);
        } catch (Exception e) {
            LOG.error("qcloud-xml-api 服务器启动失败", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean shutdown() {
        try {
            singleton.stop();
            return true;
        } catch (Throwable e) {
            LOG.error(e);
            return false;
        }
    }
}
