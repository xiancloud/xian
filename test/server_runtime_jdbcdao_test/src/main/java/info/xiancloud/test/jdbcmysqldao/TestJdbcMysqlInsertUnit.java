package info.xiancloud.test.jdbcmysqldao;

import info.xiancloud.core.Group;
import info.xiancloud.dao.core.action.SqlAction;
import info.xiancloud.dao.core.action.insert.InsertAction;
import info.xiancloud.dao.core.units.DaoUnit;

public class TestJdbcMysqlInsertUnit extends DaoUnit {
    @Override
    public SqlAction[] getActions() {
        return new SqlAction[]{
                new InsertAction() {
                    @Override
                    protected String tableName() {
                        return "xian_table";
                    }
                }
        };
    }

    @Override
    public Group getGroup() {
        return JdbcMysqlTestDaoGroup.SINGLETON;
    }
}
