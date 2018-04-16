package info.xiancloud.core.util.file;

import info.xiancloud.core.util.ArrayUtil;
import info.xiancloud.core.util.LOG;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * util class for plugin jars.
 *
 * @author happyyangyuan
 */
public class PluginFileUtil {

    private static final String PLUGINS_DIR_NAME = "plugins/";

    /**
     * @return all jars in classpath, no cache.
     */
    public static File[] jars() {
        File[] jarsInPluginsDir = jarsInPlugins(),
                jarsInLibsDir = jarsInLibs();
        return ArrayUtil.concat(jarsInLibsDir, jarsInPluginsDir);
    }

    /**
     * @return the war file in the plugins dir.
     * @throws FileNotFoundException unable to find a war file in the plugins directory.
     */
    public static File war() throws FileNotFoundException {
        String[] wars = new File(PLUGINS_DIR_NAME).list((dir, name) -> name.endsWith(".war"));
        if (wars == null || wars.length == 0) {
            throw new FileNotFoundException("unable to find a war file in " + PLUGINS_DIR_NAME);
        }
        return new File(PLUGINS_DIR_NAME + wars[0]);
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
     * @param fileName plugin jar name
     * @return plugin jar version, see build.gradle for the version configuration.
     * @throws IllegalArgumentException the jar name is not a standard name: name-version.jar
     */
    public static String version(String fileName) {
        String versionNumber = "";
        if (fileName.contains(".")) {
            String majorVersion = fileName.substring(0, fileName.indexOf("."));
            String minorVersion = fileName.substring(fileName.indexOf("."));
            int delimiter = majorVersion.lastIndexOf("-");
            if (majorVersion.indexOf("_") > delimiter) delimiter = majorVersion.indexOf("_");
            majorVersion = majorVersion.substring(delimiter + 1, fileName.indexOf("."));
            versionNumber = majorVersion + minorVersion;
            return versionNumber;
        } else
            throw new IllegalArgumentException("Not a standard jar name: " + fileName);
    }

    /**
     * @param jarName jar file name
     * @return plugin name without the version
     * @throws IllegalArgumentException the jar file name is not a standard name: name-version.jar
     */
    public static String pluginName(String jarName) {
        if (jarName.contains("-"))
            return jarName.substring(0, jarName.lastIndexOf(version(jarName)));
        throw new IllegalArgumentException("Not a standard jar name: " + jarName);
    }

}
