package info.xiancloud.test.jdbcmysqldao;

import info.xiancloud.core.Group;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.update.UpdateAction;
import info.xiancloud.dao.core.units.DaoUnit;

public class TestJdbcMysqlUpdateUnit extends DaoUnit {
    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new UpdateAction() {
                    @Override
                    protected String tableName() {
                        return "xian_table";
                    }

                    @Override
                    protected String[] searchConditions() {
                        return new String[]{
                                "id_id = 1"
                        };
                    }
                }
        };
    }

    @Override
    public Group getGroup() {
        return JdbcMysqlTestDaoGroup.SINGLETON;
    }
}
