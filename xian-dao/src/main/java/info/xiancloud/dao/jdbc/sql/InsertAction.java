package info.xiancloud.dao.jdbc.sql;

import info.xiancloud.core.Group;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.jdbc.sql.dml.IDML;
import info.xiancloud.dao.jdbc.BasicSqlBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class InsertAction extends AbstractAction implements IUnique, IDML {

    /**
     * 插入后生成的id
     */
    private Integer id;
    private String idCol;

    /**
     * @deprecated 请直接给数据库表字段定义唯一性约束来实现!
     */
    @Override
    public Object unique() {
        return "";
    }

    @Override
    protected Object executeSql(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        return insert(preparedSql, sqlParams, connection);
    }

    @Override
    protected String sqlPattern(Map map, Connection connection) throws SQLException {
        return BasicSqlBuilder.buildInserSql(table(), getCols(), map);
    }

    protected UnitResponse check(Unit daoUnit, Map map, Connection connection) {
        UnitResponse unitResponseObject = super.check(daoUnit, map, connection);
        if (!unitResponseObject.getCode().equals(Group.CODE_SUCCESS)) {
            return unitResponseObject;
        }
        try {
            return new UniqueChecker(this).checkUnique();
        } catch (SQLException e) {
            return UnitResponse.exception(e, "SQL异常");
        }
    }

    /**
     * 如果插入的记录是自动生成主键的，返回自动生成的主键id
     * 如果插入记录没有主键生成的，返回插入条数
     */
    private int insert(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(preparedSql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < sqlParams.length; i++) {
            statement.setObject(i + 1, sqlParams[i]);
        }
        statement.execute();
        ResultSet rs = statement.getGeneratedKeys();
        Integer count = statement.getUpdateCount();
        if (rs.next()) {
            id = rs.getInt(1);//TODO 目前只实现int主键类型，目前不支持组合主键
        }
        rs.close();
        statement.close();
        return id == null ? count : id;
    }

    public Integer getId() {
        return id;
    }

    public String getIdCol() {
        if (StringUtil.isEmpty(idCol)) {
            idCol = ISingleTableAction.getIdColName(table(), connection);
        }
        return idCol;
    }

    private String[] cols;

    public String[] getCols() throws SQLException {
        if (cols == null || cols.length == 0) {
            cols = ISingleTableAction.queryCols(table(), connection);
        }
        return cols;
    }
}
