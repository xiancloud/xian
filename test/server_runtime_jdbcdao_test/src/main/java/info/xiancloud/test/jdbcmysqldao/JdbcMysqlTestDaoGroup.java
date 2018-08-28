package info.xiancloud.test.jdbcmysqldao;

import info.xiancloud.dao.core.DaoGroup;

public class JdbcMysqlTestDaoGroup implements DaoGroup {
    static JdbcMysqlTestDaoGroup SINGLETON = new JdbcMysqlTestDaoGroup();
}
