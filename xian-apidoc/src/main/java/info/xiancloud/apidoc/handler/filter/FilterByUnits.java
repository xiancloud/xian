package info.xiancloud.apidoc.handler.filter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.UnitProxy;

import java.util.List;
import java.util.Objects;

/**
 * @author happyyangyuan
 */
public class FilterByUnits implements IUnitFilter {

    private List<String> units;

    @Override
    public Multimap<String, UnitProxy> filter(Multimap<String, UnitProxy> unitMap) {
        if (this.units != null)
            return filteredUnits(unitMap, this.units);
        else return unitMap;
    }

    @Override
    public void setValues(List<String> values) {
        units = values;
    }

    private static Multimap<String, UnitProxy> filteredUnits(Multimap<String, UnitProxy> units, final List<String> fullUnitNames) {
        Multimap<String, UnitProxy> results = ArrayListMultimap.create();
        for (UnitProxy unitProxy : units.values()) {
            for (String unitFullName : fullUnitNames) {
                if (Objects.equals(Unit.fullName(unitProxy), unitFullName)) {
                    results.put(unitProxy.getGroup().getName(), unitProxy);
                }
            }
        }
        return results;
    }

}
