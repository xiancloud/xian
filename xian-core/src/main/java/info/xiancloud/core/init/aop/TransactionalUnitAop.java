package info.xiancloud.core.init.aop;

import info.xiancloud.core.Constant;
import info.xiancloud.core.Group;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;
import info.xiancloud.core.aop.IUnitAop;
import info.xiancloud.core.init.IStartService;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author happyyangyuan
 * @deprecated xian frame currently can not supports distributed transactions.
 */
public class TransactionalUnitAop implements IUnitAop, IStartService {

    @Override
    public Collection<Unit> getUnitCollection() {
        Set<Unit> collection = new HashSet<>();
        LocalUnitsManager.unitMap(unitMap -> {
            for (String groupName : unitMap.keySet()) {
                for (Unit unit : unitMap.get(groupName)) {
                    if (unit.getMeta().isTransactional()) {
                        collection.add(unit);
                    }
                }
            }
        });
        return collection;
    }

    @Override
    public Object before(Unit unit, UnitRequest unitRequest) {
        //todo Please define a abstraction interface of transaction operations instead of calling the transaction operation unit.
        return SingleRxXian.call(Constant.SYSTEM_DAO_GROUP_NAME, "beginTrans", new HashMap<>());
    }

    @Override
    public void after(Unit unit, UnitRequest unitRequest, UnitResponse unitResponse, Object beforeReturn) {
        if (((UnitResponse) beforeReturn).succeeded()) {
            if (unitResponse.getContext().isRollback() || unitResponse.getData() instanceof Throwable || Group.CODE_EXCEPTION.equals(unitResponse.getCode())) {
                //todo Please to define a abstraction interface of rollbackTransaction instead of calling the rollbackTrans unit.
                SingleRxXian.call(Constant.SYSTEM_DAO_GROUP_NAME, "rollbackTrans");
            } else {
                //todo Please define a abstraction interface of transaction operations instead of calling the transaction operation unit.
                SingleRxXian.call(Constant.SYSTEM_DAO_GROUP_NAME, "commitTrans");
            }
        }
    }

    @Override
    public boolean startup() {
        LOG.info("Currently we do not start transaction aop.");
        return true;
    }

}
