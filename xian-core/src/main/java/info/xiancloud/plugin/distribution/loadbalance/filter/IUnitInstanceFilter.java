package info.xiancloud.plugin.distribution.loadbalance.filter;

import info.xiancloud.plugin.distribution.service_discovery.UnitInstance;

import java.util.Collection;
import java.util.List;

/**
 * 针对万恶的虚拟unit
 *
 * @author happyyangyuan
 */
public interface IUnitInstanceFilter {

    List<UnitInstance> filter(Collection<UnitInstance> instances);

}
