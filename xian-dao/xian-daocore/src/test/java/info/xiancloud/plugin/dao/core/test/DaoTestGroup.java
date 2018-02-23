package info.xiancloud.plugin.dao.core.test;

import info.xiancloud.plugin.dao.core.group.DaoGroup;

/**
 * for test
 *
 * @author happyyangyuan
 */
public class DaoTestGroup implements DaoGroup {
    @Override
    public String getName() {
        return "DaoTestGroup";
    }

    @Override
    public String getDescription() {
        return "DaoTestGroup";
    }

    public static final DaoTestGroup singleton = new DaoTestGroup();

}
