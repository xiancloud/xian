package info.xiancloud.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.Pair;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.Pair;
import info.xiancloud.core.util.StringUtil;

/**
 * Super interface for all service units.
 * All subclasses will be registered into the service discovery/registry center.
 *
 * @author happyyangyuan
 */
public interface Unit {

    /**
     * full unit name's separator
     */
    String SEPARATOR = ".";

    RequiredOrNot REQUIRED = RequiredOrNot.REQUIRED,
            NOT_REQUIRED = RequiredOrNot.NOT_REQUIRED;

    XhashOrNot XHASH = XhashOrNot.XHASH,
            NO_XHASH = XhashOrNot.NO_XHASH;

    SequentialOrNot SEQUENTIAL = SequentialOrNot.SEQUENTIAL,
            NO_SEQUENTIAL = SequentialOrNot.NO_SEQUENTIAL;


    /**
     * 组件业务调用时的参数定义
     */
    Input getInput();

    /**
     * @return this unit's name. Defaults to the unit class name with first char to lower case.
     */
    default String getName() {
        return StringUtil.firstCharToLowerCase(getClass().getSimpleName());
    }

    Group getGroup();

    /**
     * returns unit's meta info which describes the unit's extended definition.
     */
    default UnitMeta getMeta() {
        return UnitMeta.create().setDescription(getName());
    }

    /**
     * 组件业务执行方法
     *
     * @param request 具体的消息
     */
    UnitResponse execute(UnitRequest request);

    /**
     * 用于序列化unit定义；
     * 我们应当默认不序列化unit所有属性，只序列化指定了的属性：base、inputObjs、errorCodes；
     * 否则日后无论哪个业务unit只要有意外的getter定义就会被触发调用而出风险
     */
    default String toJSONString() {
        return JSON.toJSONString(this, filter);
    }

    //同上，用于限制unit对象只序列化指定属性，规避未知业务unit的意外getter被触发调用
    SimplePropertyPreFilter filter = new SimplePropertyPreFilter(Unit.class, "name","group","meta", "input", "version");

    /**
     * Unified unit full name. The full name is the combination of group name and unit name.
     */
    static String fullName(String groupName, String unitName) {
        return groupName.concat(SEPARATOR).concat(unitName);
    }

    /**
     * Unified unit full name. The full name is the combination of group name and unit name.
     */
    static String fullName(Unit unit) {
        return fullName(unit.getGroup().getName(), unit.getName());
    }

    /**
     * split full unit name into a pair of group and unit name.
     *
     * @return Pair. the fst is group name, the snd is unit name.
     */
    static Pair<String, String> parseFullName(String fullName) {
        String[] split = fullName.split(StringUtil.escapeSpecialChar(SEPARATOR));
        String groupName = split[0];
        String unitName = split[1];
        return Pair.of(groupName, unitName);
    }

}
