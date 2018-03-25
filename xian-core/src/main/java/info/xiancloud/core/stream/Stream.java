package info.xiancloud.core.stream;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.LOG;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * data是inputStream的unitResponse类;
 * 支持流的写入和读出，使用无界半阻塞队列，写入不阻塞，读出时阻塞
 *
 * @author happyyangyuan
 */
public class Stream extends InputStream {
    //使用阻塞队列，默认无界，读出时阻塞，写入时不阻塞。
    private LinkedBlockingQueue<StreamFragmentBean> buffer = new LinkedBlockingQueue<>();
    private StreamFragmentBean current;
    private int currentIndex = 0;
    private boolean streamEnd = false;

    @Override
    synchronized public int read() {
        if (streamEnd) return -1;
        if (current == null) {
            try {
                current = buffer.take();
                takenCount++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if ((current.getBody() == null || current.getBody().length == 0) && current.getHeader().isLast()) {
            streamEnd = true;
            return -1;
        }
        byte currentByte = current.getBody()[currentIndex];
        currentIndex++;
        if (currentIndex == current.getBody().length) {
            JSONObject jsonLog = new JSONObject();
            jsonLog.put("type", "stream");
            jsonLog.put("description", "已读取 ---------------->    " + takenCount * StreamManager.BUF_SIZE_IN_BYTE / (/*1024 * */1024d) + " kb");
            jsonLog.put("header", current.getHeader());
            LOG.info(jsonLog);
            streamEnd = current.getHeader().isLast();
            current = null;
            currentIndex = 0;
        }
        return currentByte & 255;
    }

    private int takenCount = 0;
    private int addCount = 0;

    //由于使用的是默认无界队列，因此写入是不阻塞的
    public void add(StreamFragmentBean streamFragmentBean) {
        buffer.add(streamFragmentBean);
        addCount++;
        LOG.info(new JSONObject() {{
            put("type", "stream");
            put("streamId", streamFragmentBean.getHeader().getId());
            put("description", "已收到 ---------------->    " + addCount * StreamManager.BUF_SIZE_IN_BYTE / (/*1024 * */1024d) + " kb");
            put("header", streamFragmentBean.getHeader());
        }});
    }

    @Override
    public void close() throws IOException {
        buffer.clear();
    }
}
