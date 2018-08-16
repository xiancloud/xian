package info.xiancloud.dao.core.units;

import info.xiancloud.core.ExtraUnitProvider;
import info.xiancloud.core.Group;
import info.xiancloud.core.Unit;
import info.xiancloud.dao.core.DaoGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * provides extra base dao units to units discovery.
 *
 * @author happyyangyuan
 */
public class DaoUnitProvider implements ExtraUnitProvider {

    @Override
    public List<Unit> provideExtraUnits() {
        List<Unit> baseDaoUnits = new ArrayList<>();
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
            baseDaoUnits.add(new CommitTransaction() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
            baseDaoUnits.add(new RollbackTransaction() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
            baseDaoUnits.add(new BeginTransaction() {
                @Override
                public Group getGroup() {
                    return daoGroup;
                }
            });
        }
        return baseDaoUnits;
    }

}
