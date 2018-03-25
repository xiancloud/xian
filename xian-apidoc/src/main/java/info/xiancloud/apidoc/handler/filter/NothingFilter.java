package info.xiancloud.apidoc.handler.filter;

import com.google.common.collect.Multimap;
import info.xiancloud.core.distribution.UnitProxy;

import java.util.List;

/**
 * @author happyyangyuan
 */
public class NothingFilter implements IUnitFilter {
    @Override
    public Multimap<String, UnitProxy> filter(Multimap<String, UnitProxy> units) {
        return units;
    }

    @Override
    public void setValues(List<String> values) {

    }
}
