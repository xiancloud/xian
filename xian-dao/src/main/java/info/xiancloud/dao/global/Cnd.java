package info.xiancloud.dao.global;

import info.xiancloud.core.util.ArrayUtil;
import info.xiancloud.core.util.StringUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

public class Cnd {
    public enum Type {
        equal,// 相等
        not_equal,// 不相等
        less_then,// 小于
        less_equal,// 小于等于
        greater_equal,// 大于等于
        greater_then,// 大于
        fuzzy,// 模糊匹配 %xxx%
        fuzzy_left,// 左模糊 %xxx
        fuzzy_right,// 右模糊 xxx%
        not_empty,// 不为空值的情况
        empty,// 空值的情况
        in,// 在范围内
        not_in, // 不在范围内
        between_and;// 在范围内
    }

    /**
     * 组建各种sql及赋值
     */
    @SuppressWarnings("rawtypes")
    public static void buildSQL(StringBuilder sb, Cnd.Type queryType, String fieldName, Object fieldValue, String alias, ArrayList<Object> params) {
        // 非空的时候进行设置
        if (StringUtil.notNull(fieldValue) && StringUtil.notNull(fieldName)) {
            if (Cnd.Type.equal.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " = ? ");
                params.add(fieldValue);
            } else if (Cnd.Type.not_equal.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " <> ? ");
                params.add(fieldValue);
            } else if (Cnd.Type.less_then.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " < ? ");
                params.add(fieldValue);
            } else if (Cnd.Type.less_equal.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " <= ? ");
                params.add(fieldValue);
            } else if (Cnd.Type.greater_then.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " > ? ");
                params.add(fieldValue);
            } else if (Cnd.Type.greater_equal.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " >= ? ");
                params.add(fieldValue);
            } else if (Cnd.Type.fuzzy.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " like ? ");
                params.add("%" + fieldValue + "%");
            } else if (Cnd.Type.fuzzy_left.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " like ? ");
                params.add("%" + fieldValue);
            } else if (Cnd.Type.fuzzy_right.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " like ? ");
                params.add(fieldValue + "%");
            } else if (Cnd.Type.in.equals(queryType)) {
                Object[] values = null;
                if (fieldValue instanceof Collection) {
                    values = ((Collection) fieldValue).toArray();
                } else if (fieldValue instanceof Object[]) {
                    values = ((String[]) fieldValue);
                } else if (fieldValue instanceof String) {
                    values = ((String) fieldValue).split(",");
                } else {
                    throw new IllegalArgumentException("使用IN条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔)");
                }
                StringBuilder instr = new StringBuilder();
                sb.append(" and " + alias + fieldName + " in (");
                for (Object obj : values) {
                    instr.append(!StringUtil.isEmpty(instr.toString()) ? ",?" : "?");
                    params.add(obj);
                }
                sb.append(instr + ") ");
            } else if (Cnd.Type.not_in.equals(queryType)) {
                Object[] values = null;
                if (fieldValue instanceof Collection) {
                    values = ((Collection) fieldValue).toArray();
                } else if (fieldValue instanceof Object[]) {
                    values = ((String[]) fieldValue);
                } else if (fieldValue instanceof String) {
                    values = ((String) fieldValue).split(",");
                } else {
                    throw new IllegalArgumentException("使用Not IN条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔)");
                }
                StringBuilder instr = new StringBuilder();
                sb.append(" and " + alias + fieldName + " not in (");
                for (Object obj : values) {
                    instr.append(!StringUtil.isEmpty(instr.toString()) ? ",?" : "?");
                    params.add(obj);
                }
                sb.append(instr + ") ");
            } else if (Cnd.Type.between_and.equals(queryType)) {
                Object[] values = null;
                if (fieldValue instanceof Collection) {
                    values = ((Collection) fieldValue).toArray();
                } else if (fieldValue instanceof Object[]) {
                    values = ((String[]) fieldValue);
                } else if (fieldValue instanceof String) {
                    values = ((String) fieldValue).split(",");
                } else {
                    throw new IllegalArgumentException("使用BETWEEN And条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔),且长度为2");
                }

                if (values.length != 2) {
                    throw new IllegalArgumentException(String.format("Illegal between params size:%s", values.length));
                }
                sb.append(" and (" + alias + fieldName + " between ? and ?) ");
                params.add(values[0]);
                params.add(values[1]);
            }
        } else {
            if (Cnd.Type.empty.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " is null ");
            } else if (Cnd.Type.not_empty.equals(queryType)) {
                sb.append(" and " + alias + fieldName + " is not null ");
            }
        }
    }


    public static Para rule(Column column, Object obj) {
        return rule(column.getName(), column.getClassType(), obj);
    }

    public static Para rule(String attr, Class<?> classType, Object obj) {
        if (obj instanceof String) {
            String value = (String) obj;
            if (!StringUtil.isEmpty(value)) {
                //判断属性值 between_and
                if ((classType.equals(Date.class) || classType.equals(Timestamp.class) || classType.equals(Integer.class) || classType.equals(Long.class) || classType.equals(Double.class))
                        && value.indexOf("-") != -1 && value.split("-").length == 2) {
                    return new Para(attr, Cnd.Type.between_and, value.split("-"));
                }
                //判断属性值 not equal
                else if (value.startsWith("!") && value.indexOf(",") == -1) {
                    value = value.replaceFirst("!", "");
                    return new Para(attr, Cnd.Type.not_equal, value);
                }
                //判断属性值 not in
                else if (value.startsWith("!") && value.indexOf(",") != -1) {
                    value = value.replaceFirst("!", "");
                    return new Para(attr, Cnd.Type.in, value.split("-"));
                }
                //判断属性值 in
                else if (value.indexOf(",") != -1) {
                    return new Para(attr, Cnd.Type.in, value.split(","));
                }
                //判断属性值 >=
                else if (value.startsWith(">=")) {
                    value = value.replaceFirst(">=", "");
                    return new Para(attr, Cnd.Type.greater_equal, value);
                }
                //判断属性值 >
                else if (value.startsWith(">")) {
                    value = value.replaceFirst(">", "");
                    return new Para(attr, Cnd.Type.greater_then, value);
                }
                //判断属性值 <=
                else if (value.startsWith("<=")) {
                    value = value.replaceFirst("<=", "");
                    return new Para(attr, Cnd.Type.less_equal, value);
                }
                //判断属性值 <
                else if (value.startsWith(">")) {
                    value = value.replaceFirst(">", "");
                    return new Para(attr, Cnd.Type.less_then, value);
                }
                //判断属性值 %*%
                else if (value.startsWith("%") && value.endsWith("%")) {
                    value = value.replaceFirst("%", "");
                    return new Para(attr, Cnd.Type.fuzzy, value);
                }
                //判断属性值 %*
                else if (value.startsWith("%")) {
                    value = value.replaceFirst("%", "");
                    return new Para(attr, Cnd.Type.fuzzy_left, value);
                }
                //判断属性值 *%
                else if (value.endsWith("%")) {
                    value = value.replaceFirst("%", "");
                    return new Para(attr, Cnd.Type.fuzzy_right, value);
                }
            }
        } else {
            if (obj instanceof Collection || obj instanceof Object[]) {
                return new Para(attr, Cnd.Type.in, obj);
            } else if (obj.getClass().isArray()) {
                return new Para(attr, Cnd.Type.in, ArrayUtil.toObjectArray(obj));
            }
        }
        return new Para(attr, Cnd.Type.equal, obj);
    }
}
