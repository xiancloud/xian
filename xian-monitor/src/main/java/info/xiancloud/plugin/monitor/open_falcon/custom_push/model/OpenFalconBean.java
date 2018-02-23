package info.xiancloud.plugin.monitor.open_falcon.custom_push.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import info.xiancloud.plugin.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author happyyangyuan
 */
public class OpenFalconBean {
    private String endpoint;
    private String metric;
    /**
     * 时间戳int类型，单位为秒
     */
    @JSONField(name = "timestamp")
    private long timestampInSeconds;
    private int step;
    private Number value;
    private CounterType counterType;
    private String tags;
    private Map<String, Object> tagAttributes = new TreeMap<>();
    private List<String> dashboards = new ArrayList<>();
    private List<String> titles = new ArrayList<>();


    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public long getTimestampInSeconds() {
        return timestampInSeconds;
    }

    public void setTimestampInSeconds(long timestampInSeconds) {
        this.timestampInSeconds = timestampInSeconds;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public CounterType getCounterType() {
        return counterType;
    }

    public void setCounterType(CounterType counterType) {
        this.counterType = counterType;
    }

    public String getTags() {
        return gainTags();
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public OpenFalconBean addTag(String tag, Object tagValue) {
        this.tagAttributes.put(tag, tagValue);

        return this;
    }

    private String gainTags() {
        String tags = "";
        for (Map.Entry<String, Object> entry : this.tagAttributes.entrySet()) {
            String tag = entry.getKey();
            Object tagValue = entry.getValue();
            if (tagValue == null)
                continue;

            String newTag = tag + "=" + tagValue.toString().replaceAll("\\.", "_");
            if (StringUtil.isEmpty(tags)) {
                tags = "".concat(newTag);
            } else {
                tags = tags.concat(",").concat(newTag);
            }
        }

        return tags;
    }

    public List<String> getDashboards() {
        return dashboards;
    }

    public void setDashboards(List<String> dashboards) {
        this.dashboards = dashboards;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public String grafanaMetric() {
        String metric = this.endpoint.concat("#").concat(this.metric);

        String tags = getTags();
        if (tags != null && !"".equals(tags))
            metric = metric.concat("/").concat(tags);

        return metric;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("endpoint", this.endpoint);
        jsonObject.put("metric", this.metric);
        jsonObject.put("step", this.step);
        jsonObject.put("value", this.value);
        jsonObject.put("counterType", this.counterType);
        jsonObject.put("tags", this.tags);
        jsonObject.put("titles", this.titles);
        jsonObject.put("-metric-", this.grafanaMetric());
        return jsonObject.toString();
    }

}
