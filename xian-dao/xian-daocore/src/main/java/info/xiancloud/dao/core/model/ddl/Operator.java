package info.xiancloud.dao.core.model.ddl;

import info.xiancloud.core.util.ArrayUtil;
import info.xiancloud.core.util.StringUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Operators for sql search condition
 * <p>
 * todo this implementation is not perfect
 *
 * @author happyyangyuan
 */
public enum Operator {
    /**
     * "equal" operator, eg. =
     */
    equal,
    /**
     * "not equal" operator, eg. !=
     */
    not_equal,
    /**
     * "less than" operator, eg. ＜
     */
    less_than,
    /**
     * "less or equal" operator, eg. ＜＝
     */
    less_equal,
    /**
     * "greater or equal" operator, eg. ＞=
     */
    greater_equal,
    /**
     * "greater than" operator, eg. ＞
     */
    greater_than,
    /**
     * 模糊匹配 %xxx%
     */
    fuzzy,
    /**
     * 左模糊 %xxx
     */
    fuzzy_left,
    /**
     * 右模糊 xxx%
     */
    fuzzy_right,
    /**
     * 不为空值的情况, eg. is not null
     */
    not_empty,
    /**
     * 空值的情况, eg. is null
     */
    empty,
    /**
     * 在范围内, eg. in (1,2,3)
     */
    in,
    /**
     * 不在范围内, eg. not in (1,2,3)
     */
    not_in,
    /**
     * 在范围内, eg. between 1 and 10
     */
    between_and;

    /**
     * 组建各种sql及赋值
     *
     * @param sb             sql string builder
     * @param operator       sql search condition operator
     * @param fieldName      column name
     * @param fieldValue     field value
     * @param alias          sql select alias
     * @param preparedParams parameters for prepared sql
     */
    public static void buildSQL(StringBuilder sb, Operator operator, String fieldName, Object fieldValue, String alias, List<Object> preparedParams) {
        // 非空的时候进行设置
        if (StringUtil.notNull(fieldValue) && StringUtil.notNull(fieldName)) {
            if (Operator.equal.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" = ? ");
                preparedParams.add(fieldValue);
            } else if (Operator.not_equal.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" <> ? ");
                preparedParams.add(fieldValue);
            } else if (Operator.less_than.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" < ? ");
                preparedParams.add(fieldValue);
            } else if (Operator.less_equal.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" <= ? ");
                preparedParams.add(fieldValue);
            } else if (Operator.greater_than.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" > ? ");
                preparedParams.add(fieldValue);
            } else if (Operator.greater_equal.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" >= ? ");
                preparedParams.add(fieldValue);
            } else if (Operator.fuzzy.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" like ? ");
                preparedParams.add("%" + fieldValue + "%");
            } else if (Operator.fuzzy_left.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" like ? ");
                preparedParams.add("%" + fieldValue);
            } else if (Operator.fuzzy_right.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" like ? ");
                preparedParams.add(fieldValue + "%");
            } else if (Operator.in.equals(operator)) {
                Object[] values;
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
                sb.append(" and ").append(alias).append(fieldName).append(" in (");
                for (Object obj : values) {
                    instr.append(!StringUtil.isEmpty(instr.toString()) ? ",?" : "?");
                    preparedParams.add(obj);
                }
                sb.append(instr).append(") ");
            } else if (Operator.not_in.equals(operator)) {
                Object[] values;
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
                sb.append(" and ").append(alias).append(fieldName).append(" not in (");
                for (Object obj : values) {
                    instr.append(!StringUtil.isEmpty(instr.toString()) ? ",?" : "?");
                    preparedParams.add(obj);
                }
                sb.append(instr).append(") ");
            } else if (Operator.between_and.equals(operator)) {
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
                sb.append(" and (").append(alias).append(fieldName).append(" between ? and ?) ");
                preparedParams.add(values[0]);
                preparedParams.add(values[1]);
            }
        } else {
            if (Operator.empty.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" is null ");
            } else if (Operator.not_empty.equals(operator)) {
                sb.append(" and ").append(alias).append(fieldName).append(" is not null ");
            }
        }
    }

    /**
     * build a search condition object via column and the value indication.
     *
     * @param column           table column
     * @param operatorAndValue indicates something like not equal to the value, equal to the value, greater than the value etc.
     * @return a search condition
     */
    public static SearchCondition rule(Column column, Object operatorAndValue) {
        return rule(column.getName(), column.getClassType(), operatorAndValue);
    }

    public static SearchCondition rule(String attr, Class<?> classType, Object value0) {
        if (value0 instanceof String) {
            String value = (String) value0;
            if (!StringUtil.isEmpty(value)) {
                //判断属性值 between_and
                if ((classType.equals(Date.class) ||
                        classType.equals(Timestamp.class) ||
                        classType.equals(Integer.class) ||
                        classType.equals(Long.class) ||
                        classType.equals(Double.class))
                        && value.contains("-") && value.split("-").length == 2
                        ) {
                    return new SearchCondition(attr, Operator.between_and, value.split("-"));
                }
                //判断属性值 not equal
                else if (value.startsWith("!") && !value.contains(",")) {
                    value = value.replaceFirst("!", "");
                    return new SearchCondition(attr, Operator.not_equal, value);
                }
                //判断属性值 not in
                else if (value.startsWith("!") && value.contains(",")) {
                    //todo value with "," but "," is part of the value not the key word
                    value = value.replaceFirst("!", "");
                    return new SearchCondition(attr, Operator.in, value.split("-"));
                }
                //判断属性值 in
                else if (value.contains(",")) {
                    //todo value with "," but "," is part of the value not the key word
                    return new SearchCondition(attr, Operator.in, value.split(","));
                }
                //判断属性值 >=
                else if (value.startsWith(">=")) {
                    value = value.replaceFirst(">=", "");
                    return new SearchCondition(attr, Operator.greater_equal, value);
                }
                //判断属性值 >
                else if (value.startsWith(">")) {
                    value = value.replaceFirst(">", "");
                    return new SearchCondition(attr, Operator.greater_than, value);
                }
                //判断属性值 <=
                else if (value.startsWith("<=")) {
                    value = value.replaceFirst("<=", "");
                    return new SearchCondition(attr, Operator.less_equal, value);
                }
                //判断属性值 <
                else if (value.startsWith(">")) {
                    value = value.replaceFirst(">", "");
                    return new SearchCondition(attr, Operator.less_than, value);
                }
                //判断属性值 %*%
                else if (value.startsWith("%") && value.endsWith("%")) {
                    value = value.replaceFirst("%", "");
                    return new SearchCondition(attr, Operator.fuzzy, value);
                }
                //判断属性值 %*
                else if (value.startsWith("%")) {
                    value = value.replaceFirst("%", "");
                    return new SearchCondition(attr, Operator.fuzzy_left, value);
                }
                //判断属性值 *%
                else if (value.endsWith("%")) {
                    value = value.replaceFirst("%", "");
                    return new SearchCondition(attr, Operator.fuzzy_right, value);
                }
            }
        } else {
            if (value0 instanceof Collection || value0 instanceof Object[]) {
                return new SearchCondition(attr, Operator.in, value0);
            } else if (value0.getClass().isArray()) {
                return new SearchCondition(attr, Operator.in, ArrayUtil.toObjectArray(value0));
            }
        }
        return new SearchCondition(attr, Operator.equal, value0);
    }
}
