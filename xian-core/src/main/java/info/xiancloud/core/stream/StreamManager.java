package info.xiancloud.core.stream;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author happyyangyuan
 * 流管理器
 */
public class StreamManager {

    public final static StreamManager singleton = new StreamManager();
    public final static int BUF_SIZE_IN_BYTE = 1024;

    //key是流id，当用于节点间传递流时，key使用ssid
    public static final LoadingCache<String, Stream> streamCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, Stream>() {
                @Override
                public Stream load(String ssid) throws Exception {
                    return new Stream();
                }
            });

    /**
     * 从入参片段中获取流对象并返回，如果片段属于相同的流，返回的的stream也是同一个，如果是流开头，那么pair的第二个值就是true，否则
     *
     * @param msg 必须遵循stream协议的片段字符串
     */
    public Stream add(StreamFragmentBean msg) {
        Stream stream = streamCache.getUnchecked(msg.getHeader().getId());
        stream.add(msg);
        return stream;
    }

}
