package info.xiancloud.plugin.conf.plugin;

import info.xiancloud.plugin.util.EnvUtil;

/**
 * To avoid class loading deadlock.
 *
 * @author happyyangyuan
 */
public class PluginConfigHolder {
    public static PluginConfig singleton = EnvUtil.isIDE() ? new PluginIdeConfig() : new PluginNoneIdeConfig();
}
