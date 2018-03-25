package info.xiancloud.core;

import com.alibaba.fastjson.JSON;
import info.xiancloud.core.distribution.UnitProxy;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.distribution.UnitProxy;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.StringUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Local management of the units.
 *
 * @author happyyangyuan
 */
public abstract class LocalUnitsManager {

    /**
     * For thread-safe consideration.
     * This lock make support for dynamically changing local registered unit collection.
     * eg. dynamical unit aop operation may change the unit collections concurrently in runtime situation.
     */
    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * for searching local unit list within the same group
     */
    private static Map<String, List<Unit>> unitMap;

    //for searching local unit instance by full unit name
    private static Map<String, Unit> searchUnitMap;

    //for searching local group instance by unit class
    private static Map<Class<? extends Unit>, Group> searchGroupByUnitClass;

    //for searching local group instance by group name
    private static Map<String, Group> searchGroupByNameMap;

    //for searching local unit instances use unit class
    private static Map<Class<? extends Unit>, Unit> searchUnitByClass;

    static {
        try {
            unitMap = new HashMap<String, List<Unit>>() {
                public String toString() {
                    Map<String, List<String>> map = new HashMap<>();
                    for (String serviceName : unitMap.keySet()) {
                        List<String> unitNames = new ArrayList<>();
                        for (Unit unit : unitMap.get(serviceName)) {
                            unitNames.add(unit.getName());
                        }
                        map.put(serviceName, unitNames);
                    }
                    return JSON.toJSONString(map);
                }
            };
            searchUnitMap = new HashMap<>();
            searchGroupByUnitClass = new HashMap<>();
            searchGroupByNameMap = new HashMap<>();
            searchUnitByClass = new HashMap<>();
            List<Unit> allUnitList = Reflection.getSubClassInstances(Unit.class);
            //add extra units here
            for (ExtraUnitProvider extraUnitProvider : Reflection.getSubClassInstances(ExtraUnitProvider.class)) {
                allUnitList.addAll(extraUnitProvider.provideExtraUnits());
            }
            if (allUnitList != null) {
                for (Unit unit : allUnitList) {
                    if (UnitProxy.class == unit.getClass()) {
                        //UnitProxy is not unit really, it is just a bean used to cache unit definition.
                        continue;
                    }
                    if (unit.getMeta() == null || StringUtil.isEmpty(unit.getName())) {
                        System.err.println(String.format("unit %s's name is null! Please check! %s is ignored!",
                                unit.getClass().getSimpleName(),
                                unit.getClass().getSimpleName()));
                        continue;
                    }
                    if (unit.getName().contains(Unit.SEPARATOR)) {
                        System.err.println("Unit name can not contain " + Unit.SEPARATOR + " :" + unit.getName());
                        continue;
                    }
                    if (unit.getGroup() == null) {
                        System.err.println(String.format("No group is specified for the unit with name %s! This unit is ignored!", unit.getName()));
                        continue;
                    }
                    Group group = unit.getGroup();
                    if (StringUtil.isEmpty(group.getName())) {
                        System.err.println(String.format("group: %s.getName() returns null! %s.%s is ignored!",
                                group.getClass().getSimpleName(),
                                group.getClass().getSimpleName(),
                                unit.getClass().getSimpleName()));
                        continue;
                    }
                    if (group.getName().contains(Unit.SEPARATOR)) {
                        System.err.println(Unit.SEPARATOR + " is not allowed in group name: " + group.getName());
                        continue;
                    }
                    if (unitMap.get(group.getName()) != null) {
                        unitMap.get(group.getName()).add(unit);
                    } else {
                        //我们引入了动态aop功能,它会在运行时修改静态全局变量ServiceManager.unitMap,而这个map以及map内的list是被N线程并行读的,因此这最好使用并发安全并且读成本低的CopyOnWriteArrayList
                        List<Unit> list = new CopyOnWriteArrayList<>();
                        list.add(unit);
                        unitMap.put(group.getName(), list);
                    }
                    searchUnitMap.put(group.getName() + Unit.SEPARATOR + unit.getName(), unit);
                    searchGroupByUnitClass.put(unit.getClass(), group);
                    searchGroupByNameMap.put(group.getName(), group);
                    searchUnitByClass.put(unit.getClass(), unit);
                }
            }
            System.out.println(LocalUnitsManager.class.getSimpleName() + " has finished to scan all the units:  " + unitMap.toString());
        } catch (Exception e) {
            System.err.println(LocalUnitsManager.class.getSimpleName() + " failed to scan all units, System exits with code: " + Constant.SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR);
            e.printStackTrace();
            System.exit(Constant.SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR);
        }
    }

    /**
     * Dynamically change the unit collections. Currently we use this for aop.
     */
    public static void replaceUnit(/*String group, String unitName, */ Unit newUnit) {
        String group = newUnit.getGroup().getName(), unitName = newUnit.getName();
        readWriteLock.writeLock().lock();
        try {
            //unitMap
            List<Unit> unitList = unitMap.get(group);
            unitList.removeIf(unitToRemove -> Objects.equals(unitToRemove.getName(), unitName));
            unitList.add(newUnit);

            //searchUnitByClassMap
            Map<Class<? extends Unit>, Unit> tmp = new HashMap<>();
            for (Map.Entry<Class<? extends Unit>, Unit> classUnitEntry : searchUnitByClass.entrySet()) {
                Unit unit = classUnitEntry.getValue();
                if (unit.getGroup().getName().equals(group) && unit.getName().equals(unitName)) {
                    tmp.put(classUnitEntry.getKey(), newUnit);
                    break;
                }
            }
            searchUnitByClass.putAll(tmp);

            //searchUnitMap
            searchUnitMap.put(Unit.fullName(group, unitName), newUnit);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * return the cached group singleton of the given unit class.
     */
    public static Group getGroupByUnitClass(Class<? extends Unit> unitClass) {
        readWriteLock.readLock().lock();
        try {
            return searchGroupByUnitClass.get(unitClass);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * return the cached unit singleton of the given unit class.
     */
    public static Unit getUnitByUnitClass(Class<? extends Unit> unitClass) {
        readWriteLock.readLock().lock();
        try {
            return searchUnitByClass.get(unitClass);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * @return the cached group singleton of the given name.
     */
    public static Group getGroupByName(String name) {
        readWriteLock.readLock().lock();
        try {
            return searchGroupByNameMap.get(name);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Iterate the {@link #unitMap local unit map} thread-safely.
     * This iteration is for you to read the map, not to modify the map.
     *
     * @param consumer the consumer for you to operate the map.
     */
    public static void unitMap(Consumer<Map<String, List<Unit>>> consumer) {
        readWriteLock.readLock().lock();
        try {
            consumer.accept(Collections.unmodifiableMap(unitMap));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Iterate the  {@link #unitMap local unit map}  thread-safely.
     * This iteration is for you to read the map, not to modify the map.
     *
     * @param function the function whatever you want to do with the unit map.
     */
    public static <T> T unitMap(Function<Map<String, List<Unit>>, T> function) {
        readWriteLock.readLock().lock();
        try {
            return function.apply(Collections.unmodifiableMap(unitMap));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Iterate {@link #searchUnitMap} thread-safely.
     *
     * @param consumer the operation for the unit map.
     */
    public static void searchUnitMap(Consumer<Map<String, Unit>> consumer) {
        readWriteLock.readLock().lock();
        try {
            consumer.accept(Collections.unmodifiableMap(searchUnitMap));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * @param fullUnitName group.unit
     * @return the local cached unit object, or null if not found.
     */
    public static Unit getLocalUnit(String fullUnitName) {
        readWriteLock.readLock().lock();
        try {
            return searchUnitMap.get(fullUnitName);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Get local cached unit singleton.
     * the same as {@link #getLocalUnit(String)}
     */
    public static Unit getLocalUnit(String groupName, String unitName) {
        return getLocalUnit(Unit.fullName(groupName, unitName));
    }

}
