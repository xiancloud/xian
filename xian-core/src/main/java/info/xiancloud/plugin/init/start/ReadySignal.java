package info.xiancloud.plugin.init.start;

import info.xiancloud.plugin.init.Destroyable;
import info.xiancloud.plugin.init.Initable;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.file.FileUtil;
import info.xiancloud.plugin.util.file.PlainFileUtil;

import java.nio.file.FileAlreadyExistsException;

/**
 * 就绪信号，为容器环境准备的启动项，在非容器环境下，该启动项执行与否都无所谓。
 *
 * @author happyyangyuan
 */
public class ReadySignal implements Initable, Destroyable {

    public static ReadySignal singleton = new ReadySignal();

    public void init() {
        try {
            PlainFileUtil.newFile("ready", "File existence means node ready. ");
        } catch (FileAlreadyExistsException e) {
            LOG.error("ready 文件已存在？是不是有问题？", e);
        }
    }

    @Override
    public void destroy() {
        FileUtil.deleteFile("ready");
    }

}
