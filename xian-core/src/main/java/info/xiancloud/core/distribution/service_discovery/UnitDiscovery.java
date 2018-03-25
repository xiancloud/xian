package info.xiancloud.core.distribution.service_discovery;

import info.xiancloud.core.distribution.UnitProxy;
import info.xiancloud.core.util.Reflection;

import java.util.List;

/**
 * unit discovery
 *
 * @author happyyangyuan
 */
public interface UnitDiscovery extends IDiscovery<UnitInstance, UnitProxy> {

    List<UnitDiscovery> discoveries = Reflection.getSubClassInstances(UnitDiscovery.class);
    UnitDiscovery singleton = discoveries.isEmpty() ? null : discoveries.get(0);

}
