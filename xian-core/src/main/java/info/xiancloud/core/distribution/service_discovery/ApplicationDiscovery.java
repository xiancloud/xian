package info.xiancloud.core.distribution.service_discovery;

import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.core.util.Reflection;

import java.util.List;

/**
 * application node discovery.
 *
 * @author happyyangyuan
 */
public interface ApplicationDiscovery extends IDiscovery<ApplicationInstance, NodeStatus> {
    List<ApplicationDiscovery> discoveries = Reflection.getSubClassInstances(ApplicationDiscovery.class);
    ApplicationDiscovery singleton = discoveries.isEmpty() ? null : discoveries.get(0);
}
