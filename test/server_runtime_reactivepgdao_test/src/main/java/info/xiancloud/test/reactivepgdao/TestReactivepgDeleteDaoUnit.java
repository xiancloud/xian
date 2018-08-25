package info.xiancloud.test.reactivepgdao;

import info.xiancloud.core.Group;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.delete.DeleteAction;
import info.xiancloud.dao.core.units.DaoUnit;

public class TestReactivepgDeleteDaoUnit extends DaoUnit {
    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new DeleteAction() {
                    @Override
                    protected String tableName() {
                        return "untitled_table";
                    }

                    @Override
                    protected String[] searchConditions() {
                        return new String[]{
                                "column3 = {column3}"
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
