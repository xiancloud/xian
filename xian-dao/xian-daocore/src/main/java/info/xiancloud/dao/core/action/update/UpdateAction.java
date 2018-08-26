package info.xiancloud.dao.core.action.update;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.dao.core.action.IDML;
import info.xiancloud.dao.core.action.IUnique;
import info.xiancloud.dao.core.action.SafetyChecker;
import info.xiancloud.dao.core.action.WhereAction;
import info.xiancloud.dao.core.model.sqlresult.UpdatingResult;
import info.xiancloud.dao.core.utils.JdbcPatternUtil;
import io.reactivex.Single;

import java.util.HashMap;
import java.util.Map;

/**
 * super class of sql updating action
 *
 * @author happyyangyuan
 */
public abstract class UpdateAction extends WhereAction implements IUnique, IDML {

    private String tableName;

    @Override
    public String getTableName() {
        if (tableName == null) {
            tableName = tableName();
        }
        return tableName();
    }

    /**
     * produces the table name
     *
     * @return the table name
     */
    abstract protected String tableName();

    @Override
    protected Single<UpdatingResult> executeSql() {
        LOG.info("==================update execution");
        return getSqlDriver().update(getPatternSql(), getMap());
    }

    @Override
    public UnitResponse check() {
        UnitResponse unitResponseObject = super.check();
        if (!unitResponseObject.succeeded()) {
            return unitResponseObject;
        }
        return SafetyChecker.doCheck(this, getMap());
    }

    @Override
    protected String sqlHeader() {
        Map<String, Object> data = getData(getMap());
        String[] cols = getCols();
        StringBuilder sqlSb = new StringBuilder().append("update ").append(getTableName()).append(" set ");
        boolean startSet = true;
        for (String col : cols) {
            Object ob = data.get(col);
            if (ob == null) {
                continue;
            }
            if (startSet) {
                sqlSb.append(col).append("=".concat(getCurlyBrace(col)));
                startSet = false;
            } else {
                sqlSb.append(",").append(col).append("=".concat(getCurlyBrace(col)));
            }
        }//end for
        return sqlSb.toString();
    }

    private String getCurlyBrace(String key) {
        return "{".concat(StringUtil.underlineToCamel(key)).concat("}");
    }

    /**
     * 将where用掉的排除
     */
    private Map<String, Object> getData(Map<String, Object> map) {
        Map<String, Object> data = new HashMap<>(map.size());
        data.putAll(map);
        for (Object camelKey : map.keySet()) {
            for (String where : searchConditions()) {
                if (JdbcPatternUtil.getCamelKeys(where).contains(camelKey.toString())) {
                    data.remove(camelKey.toString());
                }
            }
        }
        return StringUtil.camelToUnderline(data);
    }

}
