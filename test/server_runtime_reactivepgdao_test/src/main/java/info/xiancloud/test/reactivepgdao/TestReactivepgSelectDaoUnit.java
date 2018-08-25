package info.xiancloud.test.reactivepgdao;

import info.xiancloud.core.Group;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.select.SelectAction;
import info.xiancloud.dao.core.units.DaoUnit;

public class TestReactivepgSelectDaoUnit extends DaoUnit {
    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new SelectAction() {
                    @Override
                    protected Object selectList() {
                        return null;
                    }

                    @Override
                    protected Object sourceTable() {
                        return "untitled_table";
                    }

                    @Override
                    protected String[] searchConditions() {
                        return new String[]{
                                "column2 = {column222}"
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
