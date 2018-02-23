package info.xiancloud.plugin.distribution.service_discovery;

import info.xiancloud.plugin.distribution.GroupProxy;
import info.xiancloud.plugin.util.Reflection;

import java.util.List;

/**
 * group discovery and management
 *
 * @author happyyangyuan
 */
public interface GroupDiscovery extends IDiscovery<GroupInstance, GroupProxy> {
    List<GroupDiscovery> discoveries = Reflection.getSubClassInstances(GroupDiscovery.class);
    GroupDiscovery singleton = discoveries.isEmpty() ? null : discoveries.get(0);
}
