package info.xiancloud.core.message.sender.virtureunit;

import java.util.Map;

/**
 * @author happyyangyuan
 */
public class DefaultVirtualUnitConverter implements IVirtualUnitConverter {
    public static final IVirtualUnitConverter singleton = new DefaultVirtualUnitConverter();

    @Override
    public String getConcreteUnit(String groupName, String unitName, Map map) {
        return unitName;
    }
}
