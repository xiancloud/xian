package info.xiancloud.core.conf.plugin;

import com.alibaba.fastjson.JSON;
import info.xiancloud.core.util.file.PlainFileUtil;
import info.xiancloud.core.log.SystemOutLogger;
import info.xiancloud.core.util.file.PlainFileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;


/**
 * @author explorerlong  2016-06-13
 * @author happyyangyuan
 */
class PluginBaseConfigReader extends AbstractPluginConfigReader {

    private Class<?> clazzInJar;

    PluginBaseConfigReader(Class<?> clazzInJar, String resource) {
        this.clazzInJar = clazzInJar;
        this.resource = resource;
    }

    synchronized public void reload() {
        properties = new Properties();
        InputStream in = null;
        try {
            URL url = clazzInJar.getProtectionDomain().getCodeSource().getLocation();
            resource = resource.startsWith("/") ? resource.substring(1) : resource;
            if (!url.toExternalForm().endsWith(".jar")) {//解压环境
                URL resUrl = getResourceURL(resource);
                if (resUrl != null) {
                    in = resUrl.openStream();
                }
                if (in != null) {
                    //这里做了源配置非空判断，一旦源配置文件不存在，那么content和properties内容都是空的，但都不是null
                    content = PlainFileUtil.readFromInputStream(in);
                    properties.load(new StringReader(content));
                }
            } else {// 打包环境
                PluginJarResReader jarResReader = new PluginJarResReader(new File(url.toURI()), resource);
                content = jarResReader.content();
                properties = jarResReader.properties();
            }
        } catch (Exception e) {
            SystemOutLogger.singleton.error("加载配置文件[" + resource + "]失败", e, getClass().getName());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {
                SystemOutLogger.singleton.error("", ignored, getClass().getName());
            }
            loadedTime = System.currentTimeMillis();
        }
    }

    //IDE
    private URL getResourceURL(String resource) throws IOException {
        URL resUrl = getResourceURL(resource, clazzInJar);
        //现在baseConfigReader不再读取xianframe公共配置逻辑
        return resUrl/* == null ? getResourceURL(resource, xianframeClassInJar) : resUrl*/;
    }

    private URL getResourceURL(String resource, Class<?> clazzInJar) throws MalformedURLException {
        URL resourceURL;
        URL url = clazzInJar.getProtectionDomain().getCodeSource().getLocation();
        if (new File(url.getPath() + resource).exists()) {
            resourceURL = new URL(url.toExternalForm() + resource);
        } else {
            //如果在class文件夹classes/main内找不到
            //1.先去资源文件夹resources下找
            //2.resources下找不到再去resources/main下找
            String resourcesPath = url.getPath() + "../resources/" + resource;
            String resourcesMainPath = url.getPath() + "../../resources/main/" + resource;
            if (new File(resourcesPath).exists()) {
                resourceURL = new URL("file:" + resourcesPath);
            } else if (new File(resourcesMainPath).exists()) {
                resourceURL = new URL("file:" + resourcesMainPath);
            } else {
                resourceURL = null;
            }
        }
        return resourceURL;
    }


    public static void main(String[] args) {
        System.out.println(new PluginBaseConfigReader(PluginBaseConfigReader.class, "log4j.properties").properties());
        System.out.println(new PluginBaseConfigReader(JSON.class, "/META-INF/MANIFEST.MF").properties());
    }
}
