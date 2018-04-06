package info.xiancloud.httpclient.apache_http.pool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.falcon.AbstractDiyMonitorUnit;
import info.xiancloud.core.util.EnvUtil;

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
        return UnitMeta.createWithDescription("Http连接池状态监控")
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

        return UnitResponse.createSuccess(new JSONArray() {
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
