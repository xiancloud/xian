package info.xiancloud.core.distribution.loadbalance.filter;

import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.message.id.NodeIdBean;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 针对万恶的虚拟dao unit
 *
 * @author happyyangyuan
 */
public class DaoUnitInstanceFilter implements IUnitInstanceFilter {

    public static final IUnitInstanceFilter singleton = new DaoUnitInstanceFilter();

    /**
     * 过滤并保序,返回的顺序与application.properties配置的优先级顺序一致
     *
     * @deprecated mapped dao applications are no longer configured in the application.properties,
     * using this filter will cause a unit not available exception.
     */
    public List<UnitInstance> filter(Collection<UnitInstance> instances) {
        List<UnitInstance> filtered = new ArrayList<>();
        for (String dependentApplication : EnvUtil.getDependentApplications()) {
            for (UnitInstance instance : instances) {
                String application = NodeIdBean.parse(instance.getNodeId()).getApplication();
                if (Objects.equals(application, EnvUtil.getApplication())) {
                    LOG.debug("优先考虑本地部署的dao unit,所以第一个添加进结果集");
                    filtered.add(instance);
                } else if (Objects.equals(dependentApplication, application)) {
                    filtered.add(instance);
                }
            }
        }
        return filtered;
    }

}
