package info.xiancloud.dao.jdbc.sql;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.group.DaoGroup;

import java.util.Arrays;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public class SafetyChecker {

    /**
     * 研发校验更新和删除条件入参：每一个where条件都不允许忽略
     *
     * @deprecated 废弃严格检查方式，对业务限制太严重
     */
    static UnitResponse doStrictCheck(WhereAction action, Map map) {
        LOG.debug("db执行安全检查..." + map);
        for (String whereFragment : action.where()) {
            if (action.ignoreWhereFragment(whereFragment, map)) {
                LOG.info(String.format("WARN-安全检查不通过...检查where语句%s发现它是一个全表操作!参数=%s", whereFragment, map));
                return UnitResponse.createError(DaoGroup.CODE_LACK_OF_PARAMETER, PatternUtil.getCamelKeys(whereFragment), "缺少参数");
            }
        }
        return UnitResponse.createSuccess();
    }

    static UnitResponse doCheck(WhereAction action, Map map) {
        LOG.debug("db执行安全检查..." + map);
        String[] whereArray = action.where();
        if (whereArray == null || whereArray.length == 0) {
            return UnitResponse.createSuccess("db安全检查通过");
        }
        for (String whereFragment : whereArray) {
            if (!action.ignoreWhereFragment(whereFragment, map)) {
                return UnitResponse.createSuccess("db安全检查通过");
            }
        }
        String wheres = Arrays.toString(whereArray);
        LOG.warn(String.format("安全检查不通过...检查where语句%s发现它很可能是一个全表操作!参数=%s", wheres, map));
        return UnitResponse.createError(DaoGroup.CODE_LACK_OF_PARAMETER, PatternUtil.getCamelKeys(wheres), "缺少参数");
    }
}
