package info.xiancloud.core.message;

import info.xiancloud.core.Bean;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.GroupJudge;
import info.xiancloud.core.message.sender.IAsyncSender;
import info.xiancloud.core.message.sender.SenderFactory;
import info.xiancloud.core.message.sender.virtureunit.DefaultVirtualUnitConverter;
import info.xiancloud.core.message.sender.virtureunit.IVirtualUnitConverter;
import info.xiancloud.core.message.sender.virtureunit.VirtualDaoUnitConverter;
import io.reactivex.Single;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for calling unit locally or remotely.
 *
 * @author happyyangyuan
 */
public class SingleRxXian {

    /**
     * @param destinationUnitBean the destination unit description bean. group name and unit name required.
     * @param map                 parameters
     * @return Single
     */
    public static Single<UnitResponse> call(Unit destinationUnitBean, Map<String, Object> map) {
        return call(destinationUnitBean.getGroup().getName(), destinationUnitBean.getName(), map);
    }

    /**
     * @param destinationUnitBean the destination unit description bean. group name and unit name required.
     * @param bean                parameter bean
     * @return Single
     */
    public static Single<UnitResponse> call(Unit destinationUnitBean, Bean bean) {
        return call(destinationUnitBean.getGroup().getName(), destinationUnitBean.getName(), bean);
    }

    public static Single<UnitResponse> call(String group, String unit, Map<String, Object> map) {
        UnitRequest request = UnitRequest.create(group, unit).setArgMap(map);
        return call(request);
    }

    public static Single<UnitResponse> call(String group, String unit, Bean bean) {
        return call(group, unit, bean.toMap());
    }

    public static Single<UnitResponse> call(Class<? extends Unit> unitClass, Map<String, Object> map) {
        return call(LocalUnitsManager.getGroupByUnitClass(unitClass).getName(),
                LocalUnitsManager.getUnitByUnitClass(unitClass).getName(),
                map);
    }

    public static Single<UnitResponse> call(Class<? extends Unit> unitClass, Bean bean) {
        return call(unitClass, bean.toMap());
    }

    /**
     * Tell the DAO layer to query data from the read-only database.
     */
    public static Single<UnitResponse> readonly(String daoGroup, String daoUnit, Map<String, Object> map) {
        UnitRequest request = UnitRequest.create(daoGroup, daoUnit).setArgMap(map);
        request.getContext().setReadyOnly(true);
        return call(request);
    }

    /**
     * Tell the DAO layer to query data from the read-only database.
     */
    public static Single<UnitResponse> readonly(String daoGroup, String daoUnit, Bean bean) {
        return readonly(daoGroup, daoUnit, bean.toMap());
    }

    /**
     * call the specified unit without parameters
     */
    public static Single<UnitResponse> call(String group, String unit) {
        return SingleRxXian.call(group, unit, new HashMap<>());
    }

    /**
     * call the specified unit without parameters
     */
    public static Single<UnitResponse> call(Class<? extends Unit> unitClass) {
        return call(unitClass, new HashMap<>());
    }

    public static Single<UnitResponse> call(UnitRequest request) {
        String group = request.getContext().getGroup(),
                unit = request.getContext().getUnit();
        String concretedUnitName = getConverter(group).getConcreteUnit(group, unit, request.getArgMap());
        request.getContext().setUnit(concretedUnitName).setVirtualUnit(unit);
        return Single.create(emitter -> {
            NotifyHandler handler = new NotifyHandler() {
                @Override
                protected void handle(UnitResponse unitResponse) {
                    if (unitResponse != null)
                        emitter.onSuccess(unitResponse);
                    else {
                        emitter.onError(new Exception("unit response is null"));
                    }
                }
            };
            IAsyncSender sender = SenderFactory.getSender(request, handler);
            sender.send();
        });
    }

    private static IVirtualUnitConverter getConverter(String group) {
        if (GroupJudge.defined(group) && GroupJudge.isDao(group)) {
            return VirtualDaoUnitConverter.singleton;
        }
        return DefaultVirtualUnitConverter.singleton;
    }

}
