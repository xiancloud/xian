package info.xiancloud.apidoc.handler.filter;

import com.google.common.collect.Multimap;
import info.xiancloud.core.distribution.UnitProxy;

import java.util.List;

/**
 * @author happyyangyuan
 */
public interface IUnitFilter {

    Multimap<String, UnitProxy> filter(Multimap<String, UnitProxy> units);

    void setValues(List<String> values);

}
