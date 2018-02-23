package info.xiancloud.plugin.aop;

import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.LocalUnitsManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author happyyangyuan
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
