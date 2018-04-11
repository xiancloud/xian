package info.xiancloud.core.sequence.default_sequencer;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.LackParamException;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.sender.IAsyncSender;
import info.xiancloud.core.sequence.ISequencer;
import info.xiancloud.core.thread_pool.SingleThreadExecutorGroup;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.ThreadUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 构造worker并将worker按顺序放入缓存队列排队
 *
 * @author happyyangyuan
 */
public class DefaultSequencer implements ISequencer {
    private String group;
    private String unit;
    private JSONObject argMap;
    private Map<String, Object> sequentialData;

    /**
     * 线程组的map集合，按业务属性分为不懂的线程组，业务内保序，业务间并行而无干扰
     */
    private static LoadingCache<Integer, SingleThreadExecutorGroup> executorGroups = CacheBuilder.newBuilder()
            .build(new CacheLoader<Integer, SingleThreadExecutorGroup>() {
                public SingleThreadExecutorGroup load(Integer key) throws Exception {
                    LOG.debug("_sequential   新建sequential线程组: " + key);
                    return ThreadPoolManager.newSingleTreadExecutorGroup(ThreadUtils.CPU_CORES * 20);
                }
            });

    private static SingleThreadExecutorGroup getGroup(Collection sequential) {
        int key = computeKey(sequential);
        LOG.debug("_sequential   获取线程组，groupKey=" + key + ", 来自sequential=" + sequential);
        return executorGroups.getUnchecked(key);
    }

    //这里使用hashcode合并来取array对应的key，因此不依赖array的元素顺序，但是有一定概率会让不同的array得到相同的key。
    //不过我们对于不同的array得到相同的key，是可以容忍的。
    private static int computeKey(Collection array) {
        int intKey = 0;
        for (Object s : array) {
            intKey += s.hashCode();
        }
        return intKey;
    }

    public DefaultSequencer(String group, String unit, JSONObject argMap) {
        this.group = group;
        this.unit = unit;
        this.argMap = argMap;
    }

    @Override
    public void sequence(IAsyncSender asyncSender, NotifyHandler onFailure) {
        try {
            getSequentialData();
        } catch (LackParamException lackParam) {
            LOG.error(lackParam);
            onFailure.callback(UnitResponse.createMissingParam(lackParam.getLacedParams(), lackParam.getMessage()));
            return;
        }
        Set<String> sequential = sequentialData.keySet();
        Collection<Object> values = sequentialData.values();
        getGroup(sequential).execute(computeKey(values), () -> asyncSender.send().get());
    }

    public Map<String, Object> getSequentialData() throws LackParamException {
        if (sequentialData == null) {
            sequentialData = SequentialDataProvider.getSequentialData(group, unit, argMap);
            LOG.debug("_sequential   sequentialData=" + sequentialData);
        }
        return sequentialData;
    }
}
