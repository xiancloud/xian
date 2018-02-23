package info.xiancloud.plugin.monitor.open_falcon.custom_push;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.monitor.common.FactorCollector;
import info.xiancloud.plugin.conf.EnvConfig;
import info.xiancloud.plugin.monitor.grafana.GrafanaService;
import info.xiancloud.plugin.init.IStartService;
import info.xiancloud.plugin.socket.ConnectTimeoutException;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.HttpUtil;
import info.xiancloud.plugin.util.LOG;

import java.net.SocketTimeoutException;

/**
 * 定时采集监控数据的定时任务
 *
 * @author happyyangyuan
 */
public class FalconPushingJob implements IStartService {

    public static final int RATE_IN_SECONDS = 60;

    private volatile static int INTERVAL = 1;

    @Override
    public boolean startup() {
        ThreadPoolManager.scheduleAtFixedRate(() -> {
            JSONArray falconBeans = new JSONArray();
            JSONObject factors;
            factors = FactorCollector.collect();
            for (String metric : factors.keySet()) {
                falconBeans.addAll(new FalconBeanBuilder().buildAll(metric, factors.get(metric)));
            }
            final String falcon_transfer_url = EnvUtil.isLan() ?
                    EnvConfig.get("lan_falcon_transfer_url") :
                    EnvConfig.get("internet_falcon_transfer_url");
            try {
                HttpUtil.postWithEmptyHeader(falcon_transfer_url, falconBeans.toJSONString());
            } catch (SocketTimeoutException | ConnectTimeoutException e) {
                LOG.error(e);
            }

            if (INTERVAL % 3 == 0)
                GrafanaService.grafana(falconBeans);
            INTERVAL++;

        }, RATE_IN_SECONDS * 1000);
        return true;
    }

}
