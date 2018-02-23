package info.xiancloud.plugin.dao.core.jdbc.sql;

import info.xiancloud.plugin.dao.core.jdbc.SqlUtils;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.dao.core.group.DaoGroup;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;

import java.sql.SQLException;
import java.util.*;

/**
 * @author happyyangyuan
 * @deprecated 不建议使用, 请给db表字段定义唯一性约束来达到不允许重复的效果.
 */
public class UniqueChecker {

    private ISingleTableAction action;

    public UniqueChecker(ISingleTableAction action) {
        this.action = action;
    }

    public UnitResponse checkUnique() throws SQLException {
        if (action instanceof UpdateAction) {
            LOG.debug("不再对updateAction执行唯一性约束检查");
            return UnitResponse.success("ok");
        }
        Set<String> uniqueKeys = uniqueKeys();
        for (String col : action.getCols()) {
            for (String key : uniqueKeys) {
                if (StringUtil.underlineToCamel(col).equals(StringUtil.underlineToCamel(key))) {//都转驼峰是为了容错
                    if (queryCount(col) > 0) {
                        return UnitResponse.error(DaoGroup.CODE_REPETITION_NOT_ALLOWED, key, String.format("%s不允许重复", key));
                    }
                }
            }
        }
        return UnitResponse.success("ok");
    }

    private String buildSelectCountSql(String col) {
        String selectCountSql = "",
                key = StringUtil.underlineToCamel(col);
        if (action instanceof InsertAction) {
            selectCountSql = "select count(1) from " + action.table() + " where " + col + " = {" + key + "}";
        }
        String actualSql = SqlUtils.mapToSql(selectCountSql, ((AbstractAction) action).map);
        LOG.info(String.format("验证%s是否重复：%s", col, actualSql));
        return actualSql;
    }

    private Long queryCount(String col) throws SQLException {
        return ISelect.selectCount(buildSelectCountSql(col), new Object[0], ((AbstractAction) action).connection);
    }

    private Set<String> uniqueKeys() {
        Set<String> uniqueKeys = new HashSet<>();
        if (action instanceof IUnique) {
            IUnique uniqueAction = (IUnique) action;
            Object data = uniqueAction.unique();
            if (data instanceof Collection) {
                uniqueKeys.addAll((Collection) data);
            }
            if (data instanceof String[]) {
                Collections.addAll(uniqueKeys, (String[]) data);
            }
            if (data instanceof String) {
                String[] keys = ((String) data).split("[ {?}]|,|\n");
                Collections.addAll(uniqueKeys, keys);
            }
        }
        AbstractAction abstractAction = (AbstractAction) action;
        Iterator<String> it = uniqueKeys.iterator();
        while (it.hasNext()) {
            String uniqueKey = it.next();
            if (StringUtil.isEmpty(abstractAction.map.get(StringUtil.underlineToCamel(uniqueKey))) &&
                    StringUtil.isEmpty(abstractAction.map.get(StringUtil.camelToUnderline(uniqueKey)))) {
                it.remove();
            }
        }
        LOG.debug("db检查唯一性字段:" + uniqueKeys + "...");
        return uniqueKeys;
    }

    public static void main(String... args) {
        IUnique u = new UpdateAction() {

            @Override
            public String table() {
                return "kkkkk";
            }

            @Override
            protected String[] where() {
                return new String[0];
            }

            @Override
            public Object unique() {
                map = new HashMap<String, Object>() {{
                    put("xxx", 12);
                    put("iiii", "test");
                }};
                return "xxx,ppp iiii lll\n" +
                        "sss";
            }
        };
        System.out.println(new UniqueChecker(u).uniqueKeys());

    }

}
