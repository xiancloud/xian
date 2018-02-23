package info.xiancloud.plugin.distribution.service_discovery;

import info.xiancloud.plugin.distribution.UnitProxy;
import info.xiancloud.plugin.util.Reflection;

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
