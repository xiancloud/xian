package info.xiancloud.plugin.dao.core.test;

import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;
import info.xiancloud.plugin.dao.core.jdbc.sql.Action;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.dao.core.group.DaoGroup;

/**
 * @author happyyangyuan
 */
public class DaoTestUnit extends DaoUnit {

    @Override
    public String getName() {
        return "daoTest";
    }

    @Override
    public Group getGroup() {
        return DaoGroup.singleton;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }
}
