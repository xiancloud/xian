package info.xiancloud.core;

import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.ProxyBuilder;
import info.xiancloud.core.init.shutdown.ShutdownHook;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.ProxyBuilder;

import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author ads, happyyangyuan
 */
public abstract class BatchUnit implements ShutdownHook, Unit {

    private static final int MIN_BATCH_SIZE = 0;

    private static final String VALUES = "values";

    private static final String FLUSH = "$flush";

    private final List<Map<String, Object>> recordCacheList = new Vector<>();

    public abstract int getBatchSize();

    @Override
    public Input getInput() {
        return new Input().add(FLUSH, Boolean.class, "是否立即执行批量的操作（将缓存的内容，马上提交给DB层）", REQUIRED);
    }

    public void preBatchExecute(UnitRequest msg) {
    }

    /**
     * 组件业务执行方法
     *
     * @param msg 具体的消息
     */
    public UnitResponse execute(UnitRequest msg) {
        UnitResponse result;
        boolean flush = msg.get(FLUSH, Boolean.class, false);//是否马上提交
        msg.getArgMap().remove(FLUSH);
        //判断是否需要批量操作
        if (getBatchSize() >= MIN_BATCH_SIZE) {
            recordCacheList.add(msg.getArgMap());
            //判断是否达到批量执行的数量阈值，或者业务需要立即执行
            if (recordCacheList.size() >= getBatchSize() || flush) {
                Map<String, Object> params = new HashMap<>();
                synchronized (recordCacheList) {
                    params.put(VALUES, new ArrayList<Map>(recordCacheList));
                    recordCacheList.clear();
                }
                preBatchExecute(msg);
                result = SyncXian.call(getBatchGroupName(), getBatchUnitName(), params);
            } else {
                result = doCache(msg);
            }
        } else {//没有批量需求，立即执行
            result = SyncXian.call(getBatchGroupName(), getBatchUnitName(), msg.getArgMap());
        }
        return result;
    }

    protected abstract UnitResponse doCache(UnitRequest msg);

    /**
     * 执行shutdownHook
     */
    public boolean shutdown() {
        LOG.debug("shutdownHook列表与unit列表是分开维护的，当前shutdownHook对象与目标unit对象不是同一个对象！");
        Unit batchUnitProxy = LocalUnitsManager.getLocalUnit(getGroup().getName(), getName());
        BatchUnit batchUnit;
        //in case the unit is proxied, which means if you want to flush the non-flushed cache list, you must get the most original object.
        if (Proxy.isProxyClass(batchUnitProxy.getClass())) {
            batchUnit = (BatchUnit) ProxyBuilder.getProxyBuilder(batchUnitProxy.hashCode()).getMostOriginalObject();
        } else {
            batchUnit = (BatchUnit) batchUnitProxy;
        }
        if (!batchUnit.recordCacheList.isEmpty()) {
            final Map<String, Object> recordCache = new HashMap<>();
            synchronized (batchUnit.recordCacheList) {
                recordCache.put(VALUES, new ArrayList<>(batchUnit.recordCacheList));
                recordCacheList.clear();
            }
            SyncXian.call(getBatchGroupName(), getBatchUnitName(), recordCache);
        }
        return true;
    }

    protected abstract String getBatchGroupName();

    protected abstract String getBatchUnitName();
}
