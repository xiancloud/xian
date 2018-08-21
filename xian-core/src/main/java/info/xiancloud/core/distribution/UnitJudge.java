package info.xiancloud.core.distribution;

import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;

/**
 * Unit judge, a convenient helper class for the {@link UnitRouter}
 *
 * @author happyyangyuan
 */
public class UnitJudge {

    /**
     * Judge whether the specified unit is defined.
     * tips: This method is for high performance high concurrent, frequently exception creation, throwing and catching is bad design.
     * So we do not use {@link UnitRouter#newestDefinition(String)} to judge the unit.
     */
    public static boolean defined(String group, String unit) {
        return LocalUnitsManager.getLocalUnit(group, unit) != null ||
                UnitDiscovery.singleton != null && UnitDiscovery.singleton.newestDefinition(Unit.fullName(group, unit)) != null;
    }

    /**
     * check whether the specified unit is available.
     * tips: don't use exception catching to get the result, cause this is bad performance.
     */
    public static boolean available(String groupName, String unitName) {
        return LocalUnitsManager.getLocalUnit(groupName, unitName) != null ||
                UnitDiscovery.singleton != null && UnitDiscovery.singleton.firstInstance(Unit.fullName(groupName, unitName)) != null;
    }

    /**
     * 注意：请先使用{{@link #defined(String, String)}}检查合法性，然后再使用本方法取属性
     *
     * @return 判定指定的unit是否是广播型
     */
    public static boolean isBroadcast(String groupName, String unitName) {
        try {
            return UnitRouter.SINGLETON.newestDefinition(Unit.fullName(groupName, unitName)).getMeta().getBroadcast() != null;
        } catch (UnitUndefinedException e) {
            throw new RuntimeException("Please call UnitJudge.defined() first.", e);
        }
    }


    /**
     * 注意：请先使用{{@link #defined(String, String)}}检查合法性，然后再使用本方法取属性
     */
    public static String[] getXhash(String groupName, String unitName) {
        try {
            return UnitRouter.SINGLETON.newestDefinition(Unit.fullName(groupName, unitName)).getInput().getXhashNames();
        } catch (UnitUndefinedException e) {
            throw new RuntimeException("Please call UnitJudge.defined() first.", e);
        }
    }

    /**
     * 注意：请先使用{{@link #defined(String, String)}}检查合法性，然后再使用本方法取属性
     *
     * @return 判定unit是否定义为xhash定向型
     */
    public static boolean isXhash(String group, String unit) {
        try {
            return UnitRouter.SINGLETON.newestDefinition(Unit.fullName(group, unit)).getInput().isXhash();
        } catch (UnitUndefinedException e) {
            throw new RuntimeException("Please call UnitJudge.defined() first.", e);
        }
    }

    /**
     * 注意：请先使用{{@link #defined(String, String)}}检查合法性，然后再使用本方法取属性
     */
    public static String[] getSequential(String group, String unit) {
        try {
            return UnitRouter.SINGLETON.newestDefinition(Unit.fullName(group, unit)).getInput().getSequentialNames();
        } catch (UnitUndefinedException e) {
            throw new RuntimeException("Please call UnitJudge.defined() first.", e);
        }
    }

    /**
     * 注意：请先使用{{@link #defined(String, String)}}检查合法性，然后再使用本方法取属性
     */
    public static boolean isSequential(String group, String unit) {
        try {
            return UnitRouter.SINGLETON.newestDefinition(Unit.fullName(group, unit)).getInput().isSequential();
        } catch (UnitUndefinedException e) {
            throw new RuntimeException("Please call UnitJudge.defined() first.", e);
        }
    }

    /**
     * 先检查unit是否为transferable，如果是，直接返回true，否则再检查application是否为transferable。
     * 注意：请先使用{{@link #defined(String, String)}}检查合法性，然后再使用本方法取属性
     */
    public static boolean isTransferable(String group, String unit) {
        try {
            return UnitRouter.SINGLETON.newestDefinition(Unit.fullName(group, unit)).getMeta().isTransferable();
        } catch (UnitUndefinedException e) {
            throw new RuntimeException("Please call UnitJudge.defined() first.", e);
        }
    }


}
