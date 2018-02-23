package info.xiancloud.plugin;

/**
 * system's unit group
 *
 * @author happyyangyuan
 */
public class SystemGroup implements Group {
    @Override
    public String getName() {
        return "system";
    }

    @Override
    public String getDescription() {
        return "系统功能";
    }

    public static final SystemGroup singleton = new SystemGroup();
}
