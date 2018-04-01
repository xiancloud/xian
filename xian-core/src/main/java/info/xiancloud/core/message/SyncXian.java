package info.xiancloud.core.message;

import info.xiancloud.core.*;
import info.xiancloud.core.message.sender.IAsyncSender;
import info.xiancloud.core.message.sender.SenderFactory;
import info.xiancloud.core.message.sender.SenderFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * synchronously call the specified unit.
 * The current business thread is blocked until the response is sent back or timed out.
 * <p>
 * Do not use this class in high concurrency situations because synchronous calling blocks local thread, it is a waste of cpu and memory.
 * </p>
 *
 * @author happyyangyuan
 */
class SyncXian {

    public static UnitResponse call(UnitRequest request, long timeoutInMilli) {
        String group = request.getContext().getGroup(),
                unit = request.getContext().getUnit();
        String concreteUnitName = Xian.getConverter(group).getConcreteUnit(group, unit, request.getArgMap());
        request.getContext().setVirtualUnit(unit).setUnit(concreteUnitName);
        IAsyncSender sender = SenderFactory.getSender(request, null);
        SenderFuture future = sender.send();
        UnitResponse response;
        try {
            response = future.get(timeoutInMilli, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            //here catch the timeout exception, and set response's code to TIME_OUT.
            response = UnitResponse.createError(Group.CODE_TIME_OUT, e, e.getLocalizedMessage());
        }
        return response;
    }

    /**
     * @param destinationUnitBean the destination unit description bean. group name and unit name required.
     * @param map                 parameters
     */
    public static UnitResponse call(Unit destinationUnitBean, Map<String, Object> map) {
        return call(destinationUnitBean.getGroup().getName(), destinationUnitBean.getName(), map);
    }


    /**
     * @param destinationUnitBean the destination unit description bean. group name and unit name required.
     * @param bean                parameters bean
     */
    public static UnitResponse call(Unit destinationUnitBean, Bean bean) {
        return call(destinationUnitBean.getGroup().getName(), destinationUnitBean.getName(), bean.toMap());
    }

    /**
     * 不需要参数的unit调用而已
     */
    public static UnitResponse call(String group, String unit) {
        return call(group, unit, new HashMap<>());
    }

    public static UnitResponse readonly(String daoGroup, String daoUnit, Map<String, Object> map) {
        UnitRequest request = UnitRequest.create(daoGroup, daoUnit).setArgMap(map);
        request.getContext().setReadyOnly(true);
        return call(request, Constant.UNIT_DEFAULT_TIME_OUT_IN_MILLI);
    }

    public static UnitResponse readonly(String daoGroup, String daoUnit, Bean bean) {
        return readonly(daoGroup, daoUnit, bean.toMap());
    }

    public static UnitResponse call(Class<? extends Unit> unitClass, Map<String, Object> map, long timeoutInMilli) {
        return call(LocalUnitsManager.getGroupByUnitClass(unitClass).getName(),
                LocalUnitsManager.getUnitByUnitClass(unitClass).getName(),
                map,
                timeoutInMilli
        );
    }

    public static UnitResponse call(Class<? extends Unit> unitClass, Bean bean, long timeoutInMilli) {
        return call(unitClass, bean.toMap(), timeoutInMilli);
    }

    public static UnitResponse call(Class<? extends Unit> unitClass, Map<String, Object> map) {
        return call(LocalUnitsManager.getGroupByUnitClass(unitClass).getName(),
                LocalUnitsManager.getUnitByUnitClass(unitClass).getName(),
                map
        );
    }

    public static UnitResponse call(Class<? extends Unit> unitClass, Bean bean) {
        return call(unitClass, bean.toMap());
    }

    public static UnitResponse call(String groupName, String unitName, Map<String, Object> map) {
        return call(groupName, unitName, map, Constant.UNIT_DEFAULT_TIME_OUT_IN_MILLI);
    }

    public static UnitResponse call(String groupName, String unitName, Bean bean) {
        return call(groupName, unitName, bean.toMap());
    }

    public static UnitResponse call(String group, String unit, Map<String, Object> map, long timeoutInMilli) {
        UnitRequest request = UnitRequest.create(group, unit).setArgMap(map);
        return call(request, timeoutInMilli);
    }

    public static UnitResponse call(String group, String unit, Bean bean, long timeoutInMilli) {
        return call(group, unit, bean.toMap(), timeoutInMilli);
    }


}
