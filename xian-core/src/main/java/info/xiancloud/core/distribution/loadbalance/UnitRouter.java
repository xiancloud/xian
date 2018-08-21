package info.xiancloud.core.distribution.loadbalance;

import info.xiancloud.core.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.distribution.service_discovery.UnitInstanceIdBean;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.Node;
import info.xiancloud.core.distribution.UnitProxy;
import info.xiancloud.core.distribution.exception.UnitInstanceOfflineException;
import info.xiancloud.core.distribution.exception.UnitOfflineException;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.distribution.service_discovery.UnitInstanceIdBean;
import info.xiancloud.core.util.EnvUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * unit router, first local then remote
 *
 * @author happyyangyuan
 */
public class UnitRouter implements IRouter<UnitInstance, UnitProxy> {

    public static final UnitRouter SINGLETON = new UnitRouter();

    private UnitRouter() {
    }

    @Override
    public UnitInstance loadBalancedInstance(String unitFullName) throws UnitOfflineException, UnitUndefinedException {
        UnitInstance instance = localInstance(unitFullName);
        if (instance != null) {
            return instance;
        }
        newestDefinition(unitFullName);
        /*  do not delete this remark
        here is for old virtual dao unit, not used any more.
        if (unitFullName.startsWith(Constant.SYSTEM_DAO_GROUP_NAME.concat("."))) {
            List<UnitInstance> mappedInstances = allInstances(unitFullName);
            instance = mappedInstances.get(new Random().nextInt(mappedInstances.size()));
        } else
        */
        if (UnitDiscovery.singleton != null) {
            instance = UnitDiscovery.singleton.lb(unitFullName);
        } else {
            //service discovery(zk) is optional plugin, we shouldn't depend on it.
            //Service discovery is disabled currently, use local single node mode.
        }
        if (instance == null) {
            throw new UnitOfflineException(unitFullName);
        }
        return instance;
    }

    @Override
    public UnitInstance localInstance(String fullUnitName) {
        Unit localUnit = LocalUnitsManager.getLocalUnit(fullUnitName);
        if (localUnit != null) {
            //本地有，就直接用即可
            UnitInstance unitInstance = new UnitInstance();
            unitInstance.setPayload(UnitProxy.create(localUnit));
            unitInstance.setRegistrationTimestamp(LocalNodeManager.singleton.getSimpleStatus().getInitTime());
            unitInstance.setPort(Node.RPC_PORT);
            unitInstance.setName(fullUnitName);
            unitInstance.setEnabled(true);
            unitInstance.setAddress(EnvUtil.getLocalIp());
            unitInstance.setId(new UnitInstanceIdBean(fullUnitName, LocalNodeManager.LOCAL_NODE_ID).getUnitInstanceId());
            return unitInstance;
        }
        return null;
    }

    @Override
    public UnitInstance firstInstance(String fullUnitName) throws UnitUndefinedException, UnitOfflineException {
        UnitInstance instance = localInstance(fullUnitName);
        if (instance != null) {
            return instance;
        }
        newestDefinition(fullUnitName);
        /*  do not delete this remark
        here is for old virtual dao unit, not used any more.
        if (fullUnitName.startsWith(*//*"dao."*//*Constant.SYSTEM_DAO_GROUP_NAME.concat("."))) {
            List<UnitInstance> mappedInstances = allInstances(fullUnitName);
            instance = mappedInstances.isEmpty() ? null : mappedInstances.get(0);
        } else*/
        if (UnitDiscovery.singleton != null) {
            instance = UnitDiscovery.singleton.firstInstance(fullUnitName);
        } else {
            //service discovery(zk) is optional plugin, we shouldn't depend on it.
            //Service discovery is disabled currently, use local single node mode.
        }
        if (instance == null) {
            throw new UnitOfflineException(fullUnitName);
        }
        return instance;
    }

    /**
     * @return 返回包含指定服务的在线的客户端列表；
     * 注意：这里不再对结果做了排序，如果是由于一致性哈希算法的需要，则 Let the code which uses constant hash do the sorting。
     */
    @Override
    public List<UnitInstance> allInstances(String fullUnitName) throws UnitOfflineException, UnitUndefinedException {
        newestDefinition(fullUnitName);// for teasing.
        List<UnitInstance> instances;
        if (UnitDiscovery.singleton != null) {
            instances = UnitDiscovery.singleton.all(fullUnitName);
        } else {
            //service discovery(zk) is optional plugin, we shouldn't depend on it.
            //Service discovery is disabled currently, use local single node mode.
            instances = new ArrayList<>();
            instances.add(localInstance(fullUnitName));
        }
        if (instances.isEmpty()) {
            throw new UnitOfflineException(fullUnitName);
        }
        /*  do not delete this remark
        here is for old virtual dao unit, not used any more.
        if (fullUnitName.startsWith(Constant.SYSTEM_DAO_GROUP_NAME.concat("."))) {
            instances = DaoUnitInstanceFilter.singleton.filter(instances);
        }*/
        /*instances.sort(Comparator.comparing(UnitInstance::getNodeId));
        * Let the code which uses constant hash do the sorting*/
        return instances;
    }

    @Override
    public UnitProxy newestDefinition(String fullUnitName) throws UnitUndefinedException {
        Unit localUnit = LocalUnitsManager.getLocalUnit(fullUnitName);
        if (localUnit != null) {
            return UnitProxy.create(localUnit);
        }
        if (UnitDiscovery.singleton == null) {
            //service discovery(zk) is optional plugin, we shouldn't depend on it.
            //Service discovery is disabled currently, use local single node mode.
        } else {
            //get unit proxy from discovery cache only if unit is not found from local
            UnitProxy unitProxy = UnitDiscovery.singleton.newestDefinition(fullUnitName);
            if (unitProxy != null) {
                return unitProxy;
            }
        }
        throw new UnitUndefinedException(fullUnitName);
    }

    @Override
    public UnitInstance getInstance(String instanceId) throws UnitInstanceOfflineException {
        if (UnitDiscovery.singleton == null) {
            throw new RuntimeException("Service discovery is disabled! No service discovery plugin is provided.");
        }
        UnitInstance instance = UnitDiscovery.singleton.instance(instanceId);
        if (instance == null) {
            throw new UnitInstanceOfflineException(instanceId);
        }
        return instance;
    }

}
