package info.xiancloud.plugin.test;

import info.xiancloud.plugin.Group;

/**
 * @author happyyangyuan
 */
public class TestGroup implements Group {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "测试";
    }

    public static final TestGroup singleton = new TestGroup();
}
