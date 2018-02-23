package info.xiancloud.plugin.aop;

import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.Unit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 指定sevice下的所有unit执行aop
 *
 * @author happyyangyuan
 */
public interface IGroupAop extends IUnitAop {

    @Override
    default Collection<Unit> getUnitCollection() {
        return newUnitSet(getGroupName());
    }

    String getGroupName();

    static Set<Unit> newUnitSet(String groupName) {
        Set<Unit> unitSet = new HashSet<>();
        LocalUnitsManager.unitMap(unitMap -> {
            for (String service : unitMap.keySet()) {
                if (service.equals(groupName)) {
                    unitSet.addAll(unitMap.get(service));
                }
            }
        });
        return unitSet;
    }

}
