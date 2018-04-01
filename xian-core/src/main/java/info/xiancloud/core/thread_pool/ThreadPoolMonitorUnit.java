package info.xiancloud.core.thread_pool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.Input;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.support.falcon.AbstractDiyMonitorUnit;
import info.xiancloud.core.util.EnvUtil;

/**
 * Thread pool monitor.
 *
 * @author happyyangyuan
 */
public class ThreadPoolMonitorUnit extends AbstractDiyMonitorUnit {

    @Override
    public String getName() {
        return "threadPoolMonitor";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("线程池使用情况监控")
                .setBroadcast(UnitMeta.Broadcast.create().setAsync(false).setSuccessDataOnly(true))
                ;
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public String title() {
        return "线程池";
    }

    @Override
    public Object execute0() {
        return UnitResponse.createSuccess(new JSONArray() {{
            add(new JSONObject() {{
                put("title", "线程池");
                put("value", ThreadPoolManager.activeCount());
                put("name", "activeCount");
                put("application", EnvUtil.getApplication());
                put("nodeId", LocalNodeManager.LOCAL_NODE_ID);
            }});
//            add(new JSONObject() {{
//                put("title", "线程池");
//                put("value", ThreadPoolManager.queueSize());
//                put("name", "queueSize");
//                put("application", EnvUtil.getApplication());
//                put("nodeId", LocalNodeManager.LOCAL_NODE_ID);
//            }});
//            add(new JSONObject() {{
//                put("title", "线程池");
//                put("value", ThreadPoolManager.poolSize());
//                put("name", "poolSize");
//                put("application", EnvironmentUtil.getApplication());
//                put("nodeId", LocalNodeManager.LOCAL_NODE_ID);
//            }});
        }});
    }

}
