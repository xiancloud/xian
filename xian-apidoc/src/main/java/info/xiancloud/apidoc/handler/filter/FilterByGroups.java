package info.xiancloud.apidoc.handler.filter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import info.xiancloud.plugin.distribution.UnitProxy;

import java.util.List;
import java.util.Objects;

/**
 * @author happyyangyuan
 */
public class FilterByGroups implements IUnitFilter {

    private List<String> groups;

    @Override
    public Multimap<String, UnitProxy> filter(Multimap<String, UnitProxy> units) {
        return filterByUnitFullNames(units, groups);
    }

    @Override
    public void setValues(List<String> values) {
        groups = values;
    }


    private static Multimap<String, UnitProxy> filterByUnitFullNames(Multimap<String, UnitProxy> units, List<String> groups) {
        Multimap<String, UnitProxy> results = ArrayListMultimap.create();
        for (String groupName : units.keySet()) {
            for (String group : groups) {
                if (Objects.equals(group, groupName))
                    results.putAll(group, units.get(group));
            }
        }
        return results;
    }
}
