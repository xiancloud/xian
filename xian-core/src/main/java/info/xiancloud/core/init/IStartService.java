package info.xiancloud.core.init;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.LOG;

/**
 * 启动服务接口
 *
 * @author ads
 */
public interface IStartService {

    /**
     * 启动入口,此方法不能是长时间阻塞，建议实现为同步，以便系统可以控制启动项顺序
     */
    boolean startup();

    /**
     * 返回在整体程序启动项名称，默认为类名
     */
    default String getInitArgName() {
        return getClass().getSimpleName();
    }

    /**
     * @return 序号，越小越先执行
     */
    default float startupOrdinal() {
        LOG.debug("默认0");
        return 0;
    }

}
