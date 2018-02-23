package info.xiancloud.graylog2.unit;

import info.xiancloud.plugin.Group;

/**
 * @author happyyangyuan
 */
public class GraylogService implements Group {
    public static Group singleton = new GraylogService();

    @Override
    public String getName() {
        return "graylogService";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
