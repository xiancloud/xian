package info.xiancloud.plugin.util.file;

import info.xiancloud.plugin.util.ArrayUtil;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;

/**
 * 类似 xian_runtime/application/plugins/ 文件分析工具类
 *
 * @author happyyangyuan
 */
public class PluginFileUtil {

    /**
     * @return all jars in classpath, no cache.
     */
    public static File[] jars() {
        File[] jarsInPluginsDir = jarsInPlugins(),
                jarsInLibsDir = jarsInLibs();
        return ArrayUtil.concat(jarsInLibsDir, jarsInPluginsDir);
    }

    /**
     * tips: this method only traverse the xian_runtime/application/plugins director for the jars.
     * * No cache, scan every time.
     */
    public static File[] jarsInPlugins() {
        return jars("plugins/");
    }

    /**
     * tips: in ../libs/ dir, the jars  are the shared ones.
     * No cache, scan every time.
     *
     * @return jars in the ../libs/
     */
    public static File[] jarsInLibs() {
        return jars("../libs/");
    }

    private static File[] jars(String relativeDirName) {
        File plugins = new File(relativeDirName);
        if (!plugins.exists() || !plugins.isDirectory())
            throw new RuntimeException("Bad working dir: " + System.getProperty("usr.dir") + ". No " + relativeDirName + " sub dir found!");
        File[] jars = plugins.listFiles(fileInPluginsDir ->
                fileInPluginsDir.isFile()
                        && fileInPluginsDir.getName().endsWith(".jar")
                        && fileInPluginsDir.getName().contains("-"));
        return jars;
    }


    /**
     * @param jar 插件jar包
     * @return 插件版本号，参见build.gradle文件 版本号配置
     * @throws IllegalArgumentException 不是标准的jar包名称
     */
    public static String version(File jar) {
        if (jar.getName().contains("-"))
            return jar.getName().substring(jar.getName().lastIndexOf("-") + 1, jar.getName().length() - 4);
        throw new IllegalArgumentException("Not a standard jar name: " + jar.getName());
    }

    /**
     * @param jar 插件jar文件
     * @return 插件名
     * @throws IllegalArgumentException 不是标准的jar包名称
     */
    public static String pluginName(File jar) {
        if (jar.getName().contains("-"))
            return jar.getName().substring(0, jar.getName().lastIndexOf("-"));
        throw new IllegalArgumentException("Not a standard jar name: " + jar.getName());
    }

}
