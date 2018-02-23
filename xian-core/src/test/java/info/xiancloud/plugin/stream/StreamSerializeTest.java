package info.xiancloud.plugin.stream;

import info.xiancloud.plugin.util.file.FileUtil;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author happyyangyuan
 */
public class StreamSerializeTest {

    @Test
    public void copy() throws FileNotFoundException, InterruptedException {
        long start = System.nanoTime();
        CountDownLatch latch = new CountDownLatch(1);
        String file = "/Users/happyyangyuan/Downloads/zz.dmg";
        String newFile = "/Users/happyyangyuan/Downloads/yy.dmg";
        StreamSerializer.singleton.encodeAndApply(new FileInputStream(file), "ssid", "msgId", streamBean -> {
            Stream xianStream = StreamManager.singleton.add(streamBean);
            if (streamBean.getHeader().isFirst()) {
                new Thread(() -> {
                    try {
                        FileUtil.copyFile(xianStream, newFile);
                        latch.countDown();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            }
        });
        latch.await();
        System.out.println("cost= " + (System.nanoTime() - start) / (1000000 * 1000) + " s");
    }

    @Test
    public void copyWithoutBuffer() throws IOException {
        String file = "/Users/happyyangyuan/Downloads/zz.dmg";
        String newFile = "/Users/happyyangyuan/Downloads/yy.dmg";
        try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
            StreamSerializer.singleton.encodeAndApply(new FileInputStream(file), "ssid", "msgId",
                    o -> {
                        try {
                            outputStream.write(o.getBody());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
