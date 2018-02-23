package info.xiancloud.plugin.unitmonitor;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.aop.IUnitAop;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * unit call frequency monitor
 *
 * @author yyq
 */
public class UnitMonitorAop implements IUnitAop, IStartService {

    // 计数器map
    final static Map<String, CountLatchEntity> countLatches = new ConcurrentHashMap<>();
    private static final String MONITOR_UNIT_LIST = "monitorUnitList";

    @Override
    public Collection<Unit> getUnitCollection() {
        // 读取配置文件，获取要监控的unit
        String[] unitArray = EnvConfig.getStringArray(MONITOR_UNIT_LIST);
        try {
            if (unitArray != null && unitArray.length > 0) {
                List<String> unitList = Arrays.asList(unitArray);
                return new HashSet<Unit>() {
                    {
                        // 配置文件内指定的监控集
                        unitList.forEach(unitFullName -> {
                            // 配置文件格式 groupName.unitName
                            Pair<String, String> pair = Unit.parseFullName(unitFullName);
                            String groupName = pair.fst;
                            String unitName = pair.snd;
                            Unit unit = LocalUnitsManager.getLocalUnit(groupName, unitName);
                            if (unit != null) {
                                add(unit);
                                // 初始化对应的计数器并设置为0
                                countLatches.put(unitFullName, new CountLatchEntity());
                            }
                        });
                        LocalUnitsManager.searchUnitMap(searchUnitMap -> {
                            // unit自定义的监控集
                            for (Unit unit : searchUnitMap.values()) {
                                if (unit.getMeta().isMonitorEnabled()) {
                                    add(unit);
                                    countLatches.put(Unit.fullName(unit), new CountLatchEntity());
                                }
                            }
                        });
                    }
                };
            }
        } catch (Exception e) {
            LOG.error("unit调用频率监控AOP 从配置文件中加载要监控的unit出错", e);
        }
        return null;
    }

    @Override
    public Object before(Unit unit, UnitRequest unitRequest) throws UnitResponseReplacement {
        JSONObject beforeReturn = new JSONObject();
        String unitName = unit.getName();
        CountLatchEntity countLatchEntity = countLatches.get(Unit.fullName(unit));
        if (countLatchEntity != null) {
            // 这里类似采用分离锁的方式,各个接口对于各自的锁,降低锁的竞争和提高细粒度
            synchronized (countLatchEntity) {
                // 获取当前时间搓 单位秒
                long markTimeNow = System.currentTimeMillis() / 1000;

                if (countLatchEntity.markTime == null) { // 第一次调用
                    countLatchEntity.markTime = markTimeNow;
                    countLatchEntity.callCount.getAndIncrement();
                } else if (countLatchEntity.markTime == markTimeNow) {// 同一秒调用
                    countLatchEntity.callCount.getAndIncrement();
                } else { // 重新开始的新一秒调用
                    countLatchEntity.markTime = markTimeNow;
                    // 获取上一秒的调用次数 并重置调用统计次数为0
                    long secondCallPre = countLatchEntity.callCount.getAndSet(1);
                    if (secondCallPre > countLatchEntity.secondCall) {
                        countLatchEntity.secondCall = secondCallPre;
                    }
                }
            }
        }
        // final long currentTime = System.currentTimeMillis();
        // 调用频率数据推送
        /*
         * if ((currentTime - countLatchEntity.startTime) >= 60 * 1000) { Long
		 * interval = (currentTime - countLatchEntity.startTime) / 1000;
		 * LOG.info("吞吐量监控:" + unitName + " 在" + interval + "s内处理了" +
		 * countLatchEntity.countLatch.get() + "个消息!!");
		 * MonitorPusher.push(unitName + "Speed", 60, new JSONObject() { {
		 * put("value", (float) countLatchEntity.countLatch.get() / interval);
		 * put("nodeId", LocalNodeManager.LOCAL_NODE_ID); } }); countLatchEntity.startTime
		 * = currentTime; countLatchEntity.countLatch.set(0); }
		 */
        return beforeReturn;
    }

    @Override
    public void after(Unit unit, UnitRequest unitRequest, UnitResponse unitResponse, Object beforeReturn)
            throws UnitResponseReplacement {
    }

    private static String getGroupName(Unit unit) {
        return /*LocalUnitsManager.getGroupByUnit(unit)*/unit.getGroup().getName();
    }

    @Override
    public boolean startup() {
        intercept();
        return true;
    }
}

class CountLatchEntity {

    Long markTime;// 标记当前所在的时间搓 ，单位-秒
    long secondCall; // 秒级调用次数（只记录最高者）
    AtomicLong callCount; // 调用次数

    CountLatchEntity() {
        secondCall = 0;
        callCount = new AtomicLong(0);
    }

}
