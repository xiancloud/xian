package info.xiancloud.test.reactivepgdao;

import info.xiancloud.core.Group;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.select.SelectAction;
import info.xiancloud.dao.core.units.DaoUnit;

public class TestReactivepgDaoUnit extends DaoUnit {
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
                        return new String[0];
                    }
                }
        };
    }

    @Override
    public Group getGroup() {
        return ReactivepgTestDaoGroup.SINGLETON;
    }

    public static void main(String[] args) {
        SingleRxXian.call(TestReactivepgDaoUnit.class).subscribe(unitResponse -> {
            unitResponse.dataToException().printStackTrace();
        });
    }
}
