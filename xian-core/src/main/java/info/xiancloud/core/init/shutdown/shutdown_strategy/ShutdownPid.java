package info.xiancloud.core.init.shutdown.shutdown_strategy;

import info.xiancloud.core.util.JavaPIDUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.file.PlainFileUtil;
import info.xiancloud.core.util.JavaPIDUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.file.PlainFileUtil;

import java.nio.file.FileAlreadyExistsException;

/**
 * 通过向指定的进程发送一个sigterm实现程序平滑退出
 *
 * @author happyyangyuan
 */
public class ShutdownPid extends ShutdownStrategy {

    private static final String PID_FILE_PATH = "pid";

    @Override
    protected void prepare() {
        LOG.debug("在本地写一个pid文件记录当前进程id,提供给stop脚本读取");
        try {
            PlainFileUtil.newFile(PID_FILE_PATH, JavaPIDUtil.getPID() + "");
        } catch (FileAlreadyExistsException e) {
            LOG.warn(e.getMessage() + " 执行删除旧pid文件，然后新建pid文件。");
            PlainFileUtil.deleteFile(PID_FILE_PATH);
            try {
                PlainFileUtil.newFile(PID_FILE_PATH, JavaPIDUtil.getPID() + "");
            } catch (FileAlreadyExistsException ignored) {
                LOG.error(ignored);
            }
        }
        PlainFileUtil.deleteOnExit(PID_FILE_PATH);
    }
}
