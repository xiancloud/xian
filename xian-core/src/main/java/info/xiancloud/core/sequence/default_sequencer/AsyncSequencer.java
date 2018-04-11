package info.xiancloud.core.sequence.default_sequencer;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.LackParamException;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.sender.AbstractAsyncSender;
import info.xiancloud.core.sequence.ISequencer;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.collections.BoundedLinkedList;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * asynchronous sequencer
 *
 * @author happyyangyuan
 */
public class AsyncSequencer implements ISequencer {
    private String group;
    private String unit;
    private JSONObject argMap;
    private Map<String, Object> sequentialData;

    /**
     * 线程组的map集合，按业务属性分为不懂的线程组，业务内保序，业务间并行而无干扰
     */
    private static LoadingCache<Integer, LoadingCache<Integer, LinkedList<AbstractAsyncSender>>> senderMap = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, LoadingCache<Integer, LinkedList<AbstractAsyncSender>>>() {
                public LoadingCache<Integer, LinkedList<AbstractAsyncSender>> load(Integer key) {
                    LOG.debug("_sequential   新建sequential队列组: " + key);
                    return CacheBuilder.newBuilder()
                            .build(new CacheLoader<Integer, LinkedList<AbstractAsyncSender>>() {
                                @Override
                                public LinkedList<AbstractAsyncSender> load(Integer key) {
                                    BoundedLinkedList<AbstractAsyncSender> boundedLinkedList = new BoundedLinkedList<>();
                                    boundedLinkedList.setCapacity(XianConfig.getIntValue("xian_async_sequencer_queue_capacity", 100));
                                    return boundedLinkedList;
                                }
                            });
                }
            });


    //这里使用hashcode合并来取array对应的key，因此不依赖array的顺序，但是有一定碰撞几率。
    //不过我们这种碰撞是可以容忍的。
    private static int computeKey(Collection array) {
        int intKey = 0;
        for (Object s : array) {
            intKey += s.hashCode();
        }
        return intKey;
    }

    public AsyncSequencer(String group, String unit, JSONObject argMap) {
        this.group = group;
        this.unit = unit;
        this.argMap = argMap;
    }

    @Override
    // this method is executed in netty event loop thread pool.
    public void sequence(final AbstractAsyncSender asyncSender, final NotifyHandler onFailure) {
        try {
            getSequentialData();
        } catch (LackParamException lackParam) {
            LOG.error(lackParam);
            onFailure.callback(UnitResponse.createMissingParam(lackParam.getLacedParams(), lackParam.getMessage()));
            return;
        }
        Set<String> sequential = sequentialData.keySet();
        Collection<Object> values = sequentialData.values();
        int outerKey = computeKey(sequential);
        int innerKey = computeKey(values);
        asyncSender.getCallback().addAfter(new NotifyHandler.Action() {//todo must make sure that there is a 100% callback in a short period of time.
            @Override
            protected void run(UnitResponse out) {
                synchronized (senderMap.getUnchecked(outerKey)) {
                    //this method is executed in the callback thread
                    AbstractAsyncSender sender = senderMap.getUnchecked(outerKey).getUnchecked(innerKey).poll();
                    if (sender == asyncSender) {
                        AbstractAsyncSender nextSender = senderMap.getUnchecked(outerKey).getUnchecked(innerKey).peek();
                        if (nextSender != null) {
                            nextSender.send();
                        } else {
                            senderMap.getUnchecked(outerKey).invalidate(innerKey);
                        }
                    } else {
                        LOG.error(new Exception("This is not we expect."));
                    }
                }
            }
        });
        synchronized (senderMap.getUnchecked(outerKey)) {
            if (senderMap.getUnchecked(outerKey).getUnchecked(innerKey).isEmpty()) {
                asyncSender.send();
            }
            senderMap.getUnchecked(outerKey).getUnchecked(innerKey).add(asyncSender);
        }
    }

    public Map<String, Object> getSequentialData() throws LackParamException {
        if (sequentialData == null) {
            sequentialData = SequentialDataProvider.getSequentialData(group, unit, argMap);
            LOG.debug("_sequential   sequentialData=" + sequentialData);
        }
        return sequentialData;
    }
}
