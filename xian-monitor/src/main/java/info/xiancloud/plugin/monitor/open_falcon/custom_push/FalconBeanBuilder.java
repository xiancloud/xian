package info.xiancloud.plugin.monitor.open_falcon.custom_push;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Bean;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.plugin.monitor.common.StaticNodeIdManager;
import info.xiancloud.plugin.monitor.open_falcon.custom_push.model.CounterType;
import info.xiancloud.plugin.monitor.open_falcon.custom_push.model.OpenFalconBean;

import java.util.*;

/**
 * @author happyyangyuan
 */
public class FalconBeanBuilder {

    private int step = FalconPushingJob.RATE_IN_SECONDS;

    //在日后变复杂后，可以考虑抽出来做成builder子类
    public OpenFalconBean build(String metric, Number value) {
        OpenFalconBean falconBean = initDefault(metric);
        falconBean.setValue(value);
        return falconBean;
    }

    //在日后变复杂后，可以考虑抽出来做成builder子类
    public OpenFalconBean build(String metric, JSONObject valueJSON) {
        OpenFalconBean falconBean = initDefault(metric);
        falconBean.setValue((Number) valueJSON.get("value"));
        if (valueJSON.containsKey("title")) {
            String title = valueJSON.getString("title");
            if (title != null)
                falconBean.getTitles().addAll(new ArrayList<>(Arrays.asList(title.split(","))));
        }
        if (valueJSON.containsKey("dashboard")) {
            String dashboard = valueJSON.getString("dashboard");
            if (dashboard != null)
                falconBean.getDashboards().addAll(new ArrayList<>(Arrays.asList(dashboard.split(","))));
        }
        for (String tag : valueJSON.keySet()) {
            if ("value".equals(tag)) continue;
            //下面将每次重启都变掉的nodeId静态化
            if ("nodeId".equals(tag)) {
                falconBean.addTag("staticNodeId", StaticNodeIdManager.getStaticNodeId(valueJSON.getString("nodeId")));
//                falconBean.addTag(tag, valueJSON.get(tag));
            } else if ("application".equals(tag)) {
                LOG.debug("注意：不再加入application标签，静态staticFinalNodeId已经包含application信息了");
            } else if ("title".equals(tag)) {

            } else if ("dashboard".equals(tag)) {

            } else {
                falconBean.addTag(tag, valueJSON.get(tag));
            }
        }
        return falconBean;
    }

    //在日后变复杂后，可以考虑抽出来做成builder子类
    public List<OpenFalconBean> build(String metric, JSONArray valueJSONArray) {
        List<OpenFalconBean> beans = new ArrayList<>();
        for (Object value : valueJSONArray) {
            beans.addAll(buildAll(metric, value));
        }
        return beans;
    }

    //在日后变复杂后，可以考虑抽出来做成builder子类
    public List<OpenFalconBean> buildAll(String metric, Object value) {
        if (value == null) {
            LOG.error(new IllegalArgumentException("value不允许为空，以-1替代为异常值,_metric=" + metric));
            value = -1;
        }
        List<OpenFalconBean> falconBeans = new ArrayList<>();
        if (value instanceof Number) {
            falconBeans.add(build(metric, Reflection.toType(value, Number.class)));
            return falconBeans;
        }
        if (value instanceof Map || value instanceof Bean) {
            /**这里使用{@link Bean}标识一个对象是一个bean*/
            falconBeans.add(build(metric, Reflection.toType(value, JSONObject.class)));
            return falconBeans;
        }
        if (value instanceof Collection || value.getClass().isArray()) {
            falconBeans.addAll(build(metric, Reflection.toType(value, JSONArray.class)));
            return falconBeans;
        }

        LOG.error(new Throwable(String.format("_metric: %s, value: %s", metric, value)));
        return falconBeans;
//        throw new RuntimeException("什么鬼?? value=" + value);
    }

    private OpenFalconBean initDefault(String metric) {
        OpenFalconBean falconBean = new OpenFalconBean();
        falconBean.setCounterType(CounterType.GAUGE);
        falconBean.setEndpoint(EnvUtil.getEnv());
        falconBean.setMetric(metric);
        falconBean.setStep(step);
//        falconBean.setTags(getDefaultTags(metric));
        falconBean.setTimestampInSeconds(System.currentTimeMillis() / 1000);
        return falconBean;
    }


    private String getDefaultTags(String metric) {
        //todo 废弃，不再需要支持默认tag
        return "";
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
