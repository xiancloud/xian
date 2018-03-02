package info.xiancloud.plugin.util.file;

import info.xiancloud.plugin.util.ArrayUtil;
import info.xiancloud.plugin.util.LOG;

import java.io.File;

/**
 * util class for plugin jars.
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

    /**
     * @param relativeDirName the directory path relative to working directory.
     * @return jars in the specified directory, or empty array if the directory does not exist or is empty or contains no jar files.
     */
    private static File[] jars(String relativeDirName) {
        File plugins = new File(relativeDirName);
        if (!plugins.exists() || !plugins.isDirectory()) {
            LOG.info(". No " + relativeDirName + " sub dir found!");
            return new File[0];
        }
        File[] jars = plugins.listFiles(fileInPluginsDir ->
                fileInPluginsDir.isFile()
                        && fileInPluginsDir.getName().endsWith(".jar")
                        && fileInPluginsDir.getName().contains("-"));
        return jars;
    }


    /**
     * @param jar plugin jar file
     * @return plugin jar version, see build.gradle for the version configuration.
     * @throws IllegalArgumentException the jar file name is not a standard name: name-version.jar
     */
    public static String version(File jar) {
        return version(jar.getName());
    }

    /**
     * @param jar plugin jar file
     * @return plugin name without the version
     * @throws IllegalArgumentException the jar file name is not a standard name: name-version.jar
     */
    public static String pluginName(File jar) {
        return pluginName(jar.getName());
    }

    /**
     * @param jarName plugin jar name
     * @return plugin jar version, see build.gradle for the version configuration.
     * @throws IllegalArgumentException the jar name is not a standard name: name-version.jar
     */
    public static String version(String jarName) {
        if (jarName.contains("-"))
            return jarName.substring(jarName.lastIndexOf("-") + 1, jarName.length() - 4);
        throw new IllegalArgumentException("Not a standard jar name: " + jarName);
    }

    /**
     * @param jarName jar file name
     * @return plugin name without the version
     * @throws IllegalArgumentException the jar file name is not a standard name: name-version.jar
     */
    public static String pluginName(String jarName) {
        if (jarName.contains("-"))
            return jarName.substring(0, jarName.lastIndexOf("-"));
        throw new IllegalArgumentException("Not a standard jar name: " + jarName);
    }

}
