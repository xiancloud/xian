package info.xiancloud.core.conf.plugin;

import info.xiancloud.core.util.file.PlainFileUtil;
import info.xiancloud.core.log.SystemOutLogger;
import info.xiancloud.core.util.file.PlainFileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author happyyangyuan
 * Read configuration in the jar file <br>
 * 读取jar包内的资源
 */
public class PluginJarResReader extends AbstractPluginConfigReader {

    private File jarFile;

    public PluginJarResReader(File jarFile, String resource) {
        this.jarFile = jarFile;
        this.resource = resource;
    }

    @Override
    public void reload() {
        InputStream in = null;
        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);
            JarEntry entry = jar.getJarEntry(resource);
            if (entry != null) {
                //从模块jar包获取到配置文件...
            } else {
                /*
                baseConfigReader不再尝试获取公共配置文件...
                    这段注释先留着不要删
                    jar.close();
                    jar = new JarFile(new File(
                            xianframeClassInJar.getProtectionDomain().getCodeSource().getLocation().toURI()));
                    entry = jar.getJarEntry(resource);*/
            }
            if (entry == null) {
                return;
            }
            in = jar.getInputStream(entry);
            if (in != null) {
                //这里做了源配置非空判断，一旦源配置文件不存在，那么content和properties内容都是空的，但都不是null
                content = PlainFileUtil.readFromInputStream(in);
                properties.load(new StringReader(content));
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            try {
                if (jar != null) {
                    jar.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {
                SystemOutLogger.singleton.error(null, ignored, getClass().getName());
            }
            loadedTime = System.currentTimeMillis();
        }
    }
}
