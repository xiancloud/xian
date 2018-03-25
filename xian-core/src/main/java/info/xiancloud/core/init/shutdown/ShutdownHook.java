package info.xiancloud.core.init.shutdown;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.LOG;

/**
 * 继承此类来实现系统退出前的执行的shutdownHook
 *
 * @author happyyangyuan
 */
public interface ShutdownHook {

    /**
     * @return 执行成功返回true, 执行失败返回false
     */
    boolean shutdown();

    /**
     * @return 序号，越小越先执行
     */
    default float shutdownOrdinal() {
        LOG.debug("默认0");
        return 0;
    }


}
