package info.xiancloud.plugin.support.falcon;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.Bean;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.Reflection;

import java.util.Collection;
import java.util.Map;

/**
 * 自定义业务监控
 *
 * @author happyyangyuan
 */
public abstract class AbstractDiyMonitorUnit implements Unit {

    @Override
    public Group getGroup() {
        return DiyMonitorGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        JSONArray responseMonitor = new JSONArray();

        String title = title();
        if (title == null || "".equals(title.trim()))
            return UnitResponse.success(responseMonitor);//todo why success with empty array? not failure or exception?

        Object monitor = execute0();
        if (monitor != null) {
            if (monitor instanceof UnitResponse)
                padding(responseMonitor, ((UnitResponse) monitor).getData());
            else
                padding(responseMonitor, monitor);
        }

        return UnitResponse.success(responseMonitor);
    }

    private void padding(JSONArray responseMonitor, Object monitor) {
        if (monitor == null)
            return;

        if (monitor instanceof Number) {
            JSONObject _monitor = new JSONObject();
            _monitor.put("title", title());
            _monitor.put("dashboard", dashboard());
            _monitor.put("value", monitor);

            responseMonitor.add(_monitor);
        } else if (monitor instanceof JSONObject || monitor instanceof Map || monitor instanceof Bean) {
            JSONObject _monitor = Reflection.toType(monitor, JSONObject.class);
            _monitor.put("title", title());
            _monitor.put("dashboard", dashboard());

            responseMonitor.add(_monitor);
        } else if (monitor instanceof Collection || monitor.getClass().isArray()) {
            JSONArray jsonArray = Reflection.toType(monitor, JSONArray.class);
            for (int i = 0; i < jsonArray.size(); i++) {
                padding(responseMonitor, jsonArray.get(i));
            }
        } else {
            LOG.warn("暂时不支持识别的监控数据: " + monitor);
        }
    }

    /**
     * dashboard
     */
    public String dashboard() {
        return null;
    }

    /**
     * title
     */
    public abstract String title();

    /**
     * @return value array/ json/ bean/ value anything.
     */
    public abstract Object execute0();

}
