package info.xiancloud.test.jdbcmysqldao;

import info.xiancloud.core.Group;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.delete.DeleteAction;
import info.xiancloud.dao.core.units.DaoUnit;

public class TestJdbcMysqlDeleteUnit extends DaoUnit {
    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new DeleteAction() {
                    @Override
                    protected String tableName() {
                        return "xian_table";
                    }

                    @Override
                    protected String[] searchConditions() {
                        return new String[]{
                                "id_id = 3"
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
