package info.xiancloud.core.sequence.default_sequencer;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import info.xiancloud.core.message.sender.IAsyncSender;
import info.xiancloud.core.thread_pool.SingleThreadExecutorGroup;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.LackParamException;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.sequence.ISequencer;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.ThreadUtils;

import java.io.File;
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
    private static LoadingCache<String, SingleThreadExecutorGroup> executorGroups = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, SingleThreadExecutorGroup>() {
                public SingleThreadExecutorGroup load(String key) throws Exception {
                    LOG.debug("_sequential   新建sequential线程组: " + key);
                    return ThreadPoolManager.newSingleTreadExecutorGroup(ThreadUtils.CPU_CORES * 20);
                }
            });

    private static SingleThreadExecutorGroup getGroup(Collection sequential) {
        String key = computeKey(sequential);
        LOG.debug("_sequential   获取线程组，groupKey=" + key + ", 来自sequential=" + sequential);
        return executorGroups.getUnchecked(key);
    }

    //todo 这里array的顺序很重要，但是上层目前未进行定序(完美的解决方案是，保证json序列化和反序列化unit定义时能保证sequential入参属性顺序不变！)
    private static String computeKey(Collection array) {
        StringBuilder key = new StringBuilder();
        for (Object s : array) {
            key.append(s.toString()).append(File.separator);
        }
        return key.toString();
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
            onFailure.callback(UnitResponse.lackOfParam(lackParam.getLacedParams(), lackParam.getMessage()));
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
