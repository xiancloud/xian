package info.xiancloud.core.stream;

import info.xiancloud.core.distribution.MessageType;
import info.xiancloud.core.distribution.MessageType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author happyyangyuan
 */
public class StreamSerializer {

    public static final StreamSerializer singleton = new StreamSerializer();

    /**
     * 流编码器，边编码，边对编码结果进行处理
     */
    public void encodeAndApply(InputStream inputStream, String ssid, String msgId, Consumer<StreamFragmentBean> consumer) {
        try (final InputStream _inputStream = inputStream) {
            final int LENGTH = StreamManager.BUF_SIZE_IN_BYTE;
            int read;
            boolean first = true;
            boolean last;
            int current = -1;
            do {
                byte[] buffer = new byte[LENGTH];
                read = _inputStream.read(buffer);
                if (read == -1) {
                    last = true;
                    buffer = new byte[0];
                } else if (read < LENGTH) {
                    buffer = Arrays.copyOf(buffer, read);
                    last = true;
                } else
                    last = false;
                StreamFragmentBean.Header header = new StreamFragmentBean.Header();
                header.setFirst(first);
                header.setId(ssid);
                header.setLast(last);
                header.setMsgId(msgId);
                header.setIndex(++current);
                StreamFragmentBean streamFragmentBean = new StreamFragmentBean();
                streamFragmentBean.setBody(buffer);
                streamFragmentBean.setHeader(header);
                streamFragmentBean.setMessageType(MessageType.responseStream);
                consumer.accept(streamFragmentBean);
                first = false;
            }
            while (!last);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
