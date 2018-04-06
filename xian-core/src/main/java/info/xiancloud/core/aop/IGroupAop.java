package info.xiancloud.core.aop;

import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 指定sevice下的所有unit执行aop
 *
 * @author happyyangyuan
 * @deprecated this won't work for asynchronous xian
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
