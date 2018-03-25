package info.xiancloud.zookeeper.unit;

import info.xiancloud.core.Group;

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
