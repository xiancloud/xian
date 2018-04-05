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
import io.reactivex.Observable;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for calling unit locally or remotely.
 *
 * @author happyyangyuan
 */
public class ObservableRxXian {

    /**
     * @param destinationUnitBean the destination unit description bean. group name and unit name required.
     * @param map                 parameters
     * @return Observable
     */
    public static Observable<UnitResponse> call(Unit destinationUnitBean, Map<String, Object> map) {
        return call(destinationUnitBean.getGroup().getName(), destinationUnitBean.getName(), map);
    }

    /**
     * @param destinationUnitBean the destination unit description bean. group name and unit name required.
     * @param bean                parameter bean
     * @return Observable
     */
    public static Observable<UnitResponse> call(Unit destinationUnitBean, Bean bean) {
        return call(destinationUnitBean.getGroup().getName(), destinationUnitBean.getName(), bean);
    }

    public static  Observable<UnitResponse> call(String group, String unit, Map<String, Object> map) {
        UnitRequest request = UnitRequest.create(group, unit).setArgMap(map);
        return call(request);
    }

    public static  Observable<UnitResponse> call(String group, String unit, Bean bean) {
        return call(group, unit, bean.toMap());
    }

    public static  Observable<UnitResponse> call(Class<? extends Unit> unitClass, Map<String, Object> map) {
        return call(LocalUnitsManager.getGroupByUnitClass(unitClass).getName(),
                LocalUnitsManager.getUnitByUnitClass(unitClass).getName(),
                map);
    }

    public static  Observable<UnitResponse> call(Class<? extends Unit> unitClass, Bean bean) {
        return call(unitClass, bean.toMap());
    }

    /**
     * Tell the DAO layer to query data from the read-only database.
     */
    public static  Observable<UnitResponse> readonly(String daoGroup, String daoUnit, Map<String, Object> map) {
        UnitRequest request = UnitRequest.create(daoGroup, daoUnit).setArgMap(map);
        request.getContext().setReadyOnly(true);
        return call(request);
    }

    /**
     * Tell the DAO layer to query data from the read-only database.
     */
    public static  Observable<UnitResponse> readonly(String daoGroup, String daoUnit, Bean bean) {
        return readonly(daoGroup, daoUnit, bean.toMap());
    }

    /**
     * call the specified unit without parameters
     */
    public static  Observable<UnitResponse> call(String group, String unit) {
        return ObservableRxXian.call(group, unit, new HashMap<>());
    }

    /**
     * call the specified unit without parameters
     */
    public static  Observable<UnitResponse> call(Class<? extends Unit> unitClass) {
        return call(unitClass, new HashMap<>());
    }

    public static  Observable<UnitResponse> call(UnitRequest request) {
        String group = request.getContext().getGroup(),
                unit = request.getContext().getUnit();
        String concretedUnitName = getConverter(group).getConcreteUnit(group, unit, request.getArgMap());
        request.getContext().setUnit(concretedUnitName).setVirtualUnit(unit);
        return Observable.create(emitter -> {
            NotifyHandler handler = new NotifyHandler() {
                @Override
                protected void handle(UnitResponse unitResponse) {
                    if (unitResponse != null)
                        emitter.onNext(unitResponse);
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
