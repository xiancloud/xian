package info.xiancloud.plugin.message.sender.virtureunit;

import java.util.Map;

/**
 * 虚拟unit名称构建
 *
 * @author happyyangyuan
 */
public interface IVirtualUnitConverter {

    /**
     * 根据传入的unit名称，如果其已经是一个实体unit，那么直接原样返回；
     * 否则获取其对应的实体unit名，如果匹配到了对应的实体unit名，那么将其返回，如果匹配不到就也原样返回，交给后面程序处理；
     *
     * @param groupName unit服务组名称
     * @param unitName  虚拟unit名称，当然你也可以传入实体unit名称，它会原样返回
     * @param map       unit的入参map，子类实现会根据需求对map内元素做小微改动
     * @return 根据传入的unit名称获取其对应的实体unit名
     */
    String getConcreteUnit(String groupName, String unitName, Map<String, Object> map);
}
