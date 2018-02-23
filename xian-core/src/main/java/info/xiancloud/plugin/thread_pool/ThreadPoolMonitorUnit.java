package info.xiancloud.plugin.thread_pool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.support.falcon.AbstractDiyMonitorUnit;
import info.xiancloud.plugin.util.EnvUtil;

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
        return UnitResponse.success(new JSONArray() {{
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
