package info.xiancloud.plugin.web;

import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;
import info.xiancloud.plugin.util.LOG;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

/**
 * Web starter
 *
 * @author happyyangyuan、yyq
 */
public class WebStartup implements IStartService {

    @Override
    public boolean startup() {

        LOG.info("---开始启动jetty----");


        final Server server = new Server(8080);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        //System.getProperty("user.dir") + "/target/httpsweb.jar")
        File warFile = new File("plugins/web.war");
        //File warFile = new File("E:\\xian\\xian_runtime\\hellowebstart\\war\\helloweb.war");
        if (!warFile.exists()) {
            LOG.error(new RuntimeException("unable to find war file :" + warFile.getAbsolutePath()));
            throw new RuntimeException("unable to find war file :" + warFile.getAbsolutePath());
        }

        LOG.info("---war包路径----" + warFile.getAbsolutePath());

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

        LOG.info("jetty启动完成.....");

        return true;
    }

}