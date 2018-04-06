package info.xiancloud.core.aop;

import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author happyyangyuan
 * @deprecated this won't work for asynchronous xian
 */
public interface IAllUnitsAop extends IUnitAop {

    default Collection<Unit> getUnitCollection() {
        Set<Unit> unitSet = new HashSet<>();
        LocalUnitsManager.unitMap(unitMap -> {
            for (List<Unit> unitList : unitMap.values()) {
                unitSet.addAll(unitList);
            }
        });
        return unitSet;
    }

}
