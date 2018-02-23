package info.xiancloud.plugin.dao.core.group.unit.base;

import info.xiancloud.plugin.ExtraUnitProvider;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.dao.core.group.DaoGroup;
import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * provides extra base dao units to units discovery.
 *
 * @author happyyangyuan
 */
public class BaseDaoUnitProvider implements ExtraUnitProvider {

    @Override
    public List<DaoUnit> provideExtraUnits() {
        List<DaoUnit> baseDaoUnits = new ArrayList<>();
        for (DaoGroup daoGroup : DaoGroup.groupList) {
            baseDaoUnits.add(new BaseAddDB() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
            baseDaoUnits.add(new BaseDeleteByIdDB() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
            baseDaoUnits.add(new BaseDeleteDB() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
            baseDaoUnits.add(new BasePaginateDB() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
            baseDaoUnits.add(new BaseQueryByIdDB() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
            baseDaoUnits.add(new BaseQueryDB() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
            baseDaoUnits.add(new BaseUpdateByIdDB() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
            baseDaoUnits.add(new BaseUpdateDB() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
        }
        return baseDaoUnits;
    }

}
