package info.xiancloud.plugin.monitor.open_falcon.custom_push;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.init.IStartService;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.HttpUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.plugin.monitor.common.FactorCollector;
import info.xiancloud.plugin.monitor.grafana.GrafanaService;

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
                    XianConfig.get("lan_falcon_transfer_url") :
                    XianConfig.get("internet_falcon_transfer_url");
            HttpUtil
                    .postWithEmptyHeader(falcon_transfer_url, falconBeans.toJSONString())
                    .subscribe(LOG::info, LOG::error);

            if (INTERVAL % 3 == 0)
                GrafanaService.grafana(falconBeans);
            INTERVAL++;

        }, RATE_IN_SECONDS * 1000);
        return true;
    }

}
