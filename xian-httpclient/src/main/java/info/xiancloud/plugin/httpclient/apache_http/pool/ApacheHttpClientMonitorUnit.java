package info.xiancloud.plugin.httpclient.apache_http.pool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.distribution.LocalNodeManager;
import info.xiancloud.plugin.support.falcon.AbstractDiyMonitorUnit;
import info.xiancloud.plugin.util.EnvUtil;

/**
 * http连接池监控
 *
 * @author happyyangyuan
 */
public class ApacheHttpClientMonitorUnit extends AbstractDiyMonitorUnit {
    @Override
    public String getName() {
        return "apacheHttpPoolMonitor";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("Http连接池状态监控")
                .setBroadcast(UnitMeta.Broadcast.create().setAsync(false).setSuccessDataOnly(true))
                .setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input();
    }

    @Override
    public String title() {
        return "http连接池";
    }

    @Override
    public Object execute0() {

        return UnitResponse.success(new JSONArray() {
            {
                add(new JSONObject() {
                    {
                        put("title", "http连接池");
                        put("value", ApacheHttpConnManager.getPending());
                        put("name", "pending");
                        put("application", EnvUtil.getApplication());
                        put("nodeId", LocalNodeManager.LOCAL_NODE_ID);
                    }
                });

                add(new JSONObject() {
                    {
                        put("title", "http连接池");
                        put("value", ApacheHttpConnManager.getAvailable());
                        put("name", "available");
                        put("application", EnvUtil.getApplication());
                        put("nodeId", LocalNodeManager.LOCAL_NODE_ID);
                    }
                });
            }
        });
    }
}
