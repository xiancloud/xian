package info.xiancloud.core.distribution.loadbalance.filter;

import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;

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
