package info.xiancloud.plugin.unit;

import info.xiancloud.plugin.Group;

/**
 * @author happyyangyuan
 */
public class ZookeeperGroup implements Group {
    public static final ZookeeperGroup singleton = new ZookeeperGroup();

    @Override
    public String getName() {
        return "zookeeper";
    }

    @Override
    public String getDescription() {
        return "zookeeper unit group";
    }

}
