package info.xiancloud.grafana;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.monitor.common.FactorCollector;
import info.xiancloud.plugin.monitor.grafana.GrafanaService;
import info.xiancloud.plugin.monitor.open_falcon.custom_push.FalconBeanBuilder;

/**
 * @author John_zero
 */
public class GrafanaTest {

    public static void main(String[] args) {
        JSONArray falconBeans = new JSONArray();

        JSONObject factors = FactorCollector.collect();
        for (String metric : factors.keySet())
            falconBeans.addAll(new FalconBeanBuilder().buildAll(metric, factors.get(metric)));

        GrafanaService.grafana(falconBeans);
    }

}
