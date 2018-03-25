package info.xiancloud.qcloudcos.unit;

import info.xiancloud.core.Group;

/**
 * @author happyyangyuan
 */
public class CosGroup implements Group {
    @Override
    public String getName() {
        return "cosService";
    }

    @Override
    public String getDescription() {
        return null;
    }

    public static final CosGroup singleton = new CosGroup();
}
