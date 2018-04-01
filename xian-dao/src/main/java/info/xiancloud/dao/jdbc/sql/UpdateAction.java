package info.xiancloud.dao.jdbc.sql;

import info.xiancloud.core.Group;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.group.DaoGroup;
import info.xiancloud.dao.jdbc.sql.dml.IDML;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public abstract class UpdateAction extends WhereAction implements IUnique, IDML {

    private String[] cols;

    /**
     * @deprecated 不建议使用, 请直接在db给指定字段增加唯一性约束
     */
    @Override
    public Object unique() {
        return "";
    }

    @Override
    protected Object executeSql(String preparedSql, Object[] sqlParams, Connection connection) throws SQLException {
        return ISingleTableAction.dosql(preparedSql, sqlParams, connection);
    }

    protected UnitResponse check(Unit daoUnit, Map map, Connection connection) {
        UnitResponse unitResponseObject = super.check(daoUnit, map, connection);
        if (!unitResponseObject.getCode().equals(Group.CODE_SUCCESS)) {
            return unitResponseObject;
        }
        UnitResponse safetyUnitResponse = SafetyChecker.doCheck(this, map);
        if (!safetyUnitResponse.getCode().equals(DaoGroup.CODE_SUCCESS)) {
            return safetyUnitResponse;
        }
        try {
            return new UniqueChecker(this).checkUnique();
        } catch (SQLException e) {
            LOG.error(e);
            return UnitResponse.createError(DaoGroup.CODE_SQL_ERROR, e, "SQL异常");
        }
    }

    @Override
    protected String sqlHeader(Map map, Connection connection) throws SQLException {
        Map data = getData(map);
        String[] cols = getCols();
        StringBuffer sqlSb = new StringBuffer();
        sqlSb.append("update " + table() + " set ");
        boolean startSet = true;
        for (int i = 0; i < cols.length; i++) {
            Object ob = data.get(cols[i]);
            if (ob == null) {
                continue;
            }
            if (startSet) {
                sqlSb.append(cols[i] + "=".concat(getCurlyBrace(cols[i])));
                startSet = false;
            } else {
                sqlSb.append("," + cols[i] + "=".concat(getCurlyBrace(cols[i])));
            }
        }//end for
        return sqlSb.toString();
    }

    private String getCurlyBrace(String key) {
        return "{".concat(StringUtil.underlineToCamel(key)).concat("}");
    }

    //将where用掉的排除
    private Map getData(Map map) {
        Map data = new HashMap<>();
        data.putAll(map);
        for (Object camelKey : map.keySet()) {
            for (String where : where()) {
                if (PatternUtil.getCamelKeys(where).contains(camelKey)) {
                    data.remove(camelKey);
                }
            }
        }
        return StringUtil.camelToUnderline(data);
    }

    public String[] getCols() throws SQLException {
        if (cols == null || cols.length == 0) {
            cols = ISingleTableAction.queryCols(table(), connection);
        }
        return cols;
    }

}
