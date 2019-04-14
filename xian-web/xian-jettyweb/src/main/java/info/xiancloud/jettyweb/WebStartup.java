package info.xiancloud.jettyweb;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.init.IStartService;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.file.PluginFileUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Web starter
 *
 * @author happyyangyuan、yyq
 */
public class WebStartup implements IStartService {

    @Override
    public boolean startup() {
        LOG.info("---Starting up jetty----");
        final Server server = new Server(XianConfig.getInteger("embeddedJavaWebPort", 8080));
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setLogUrlOnStart(true);
        File warFile;
        try {
            warFile = PluginFileUtil.war();
        } catch (FileNotFoundException e) {
            LOG.error(e);
            return false;
        }
        LOG.info("---war absolute path----" + warFile.getAbsolutePath());
        webAppContext.setWar(warFile.getAbsolutePath());
        webAppContext.setExtractWAR(true);
        server.setHandler(webAppContext);
        try {
            //jvm退出时关闭server
            server.setStopAtShutdown(true);
            server.start();
            ThreadPoolManager.execute(() -> {
                try {
                    server.join();
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
            });
        } catch (Exception e) {
            LOG.error(e);
        }
        LOG.info("jetty finished starting up .....");
        return true;
    }

}