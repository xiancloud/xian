package info.xiancloud.core.conf.plugin;

import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.EnvUtil;

/**
 * To avoid class loading deadlock.
 *
 * @author happyyangyuan
 */
public class PluginConfigHolder {
    public static PluginConfig singleton = EnvUtil.isIDE() ? new PluginIdeConfig() : new PluginNoneIdeConfig();
}
