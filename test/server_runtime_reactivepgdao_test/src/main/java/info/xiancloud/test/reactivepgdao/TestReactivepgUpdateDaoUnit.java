package info.xiancloud.test.reactivepgdao;

import info.xiancloud.core.Group;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.update.UpdateAction;
import info.xiancloud.dao.core.units.DaoUnit;

public class TestReactivepgUpdateDaoUnit extends DaoUnit {
    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new UpdateAction() {
                    @Override
                    protected String tableName() {
                        return "untitled_table";
                    }

                    @Override
                    protected String[] searchConditions() {
                        return new String[]{
                                "column2 = {column2}"
                        };
                    }
                }
        };
    }

    @Override
    public Group getGroup() {
        return ReactivepgTestDaoGroup.SINGLETON;
    }
}
