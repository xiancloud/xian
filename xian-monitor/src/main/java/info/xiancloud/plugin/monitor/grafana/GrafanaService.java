package info.xiancloud.plugin.monitor.grafana;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.plugin.monitor.open_falcon.custom_push.model.OpenFalconBean;

import java.util.*;

public class GrafanaService {

    private static final String[] LETTER = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static final String DEFAULT_DASHBOARD = EnvUtil.getShortEnvName();

    private static final List<String> REF_ID = initRef();

    private static List<String> initRef() {
        List<String> refId = new ArrayList<>();
        refId.addAll(Arrays.asList(LETTER));

        int lenth = LETTER.length;
        for (int i = 0; i < lenth; i++) {
            for (int k = 0; k < lenth; k++) {
                refId.add(LETTER[i] + LETTER[k]);
            }
        }

        return refId;
    }

    public static void grafana(JSONArray falconBeans) {
        long startNanoTime = System.nanoTime();

        if (falconBeans == null || falconBeans.isEmpty())
            return;

//        LOG.info("定时采集监控数据: " + falconBeans.toJSONString());

        /**
         * Map<Dashboard, Map<Panel, List<OpenFalconBean>>>
         */
        Map<String, Map<String, List<OpenFalconBean>>> groups = new HashMap<>();
        for (int i = 0; i < falconBeans.size(); i++) {
            OpenFalconBean openFalconBean = (OpenFalconBean) falconBeans.get(i);

            List<String> titles = openFalconBean.getTitles();
            if (titles == null || titles.isEmpty())
                continue;

            List<String> dashboards = openFalconBean.getDashboards();
            if (dashboards == null || dashboards.isEmpty())
                dashboards = Arrays.asList(DEFAULT_DASHBOARD);

            for (String dashboard : dashboards) {
                if (!groups.containsKey(dashboard))
                    groups.put(dashboard, new HashMap<>());

                for (String title : titles) {
                    if (!groups.get(dashboard).containsKey(title))
                        groups.get(dashboard).put(title, new ArrayList<>());
                    groups.get(dashboard).get(title).add(openFalconBean);
                }
            }
        }

        Map<String, JSONObject> dashboards = new HashMap<>();

        for (Map.Entry<String, Map<String, List<OpenFalconBean>>> group_dashboard : groups.entrySet()) {
            String slug = group_dashboard.getKey();
            UnitResponse unitResponseObject = SyncXian.call("grafanaService", "dashboardGet", new HashMap() {{
                put("slug", slug);
            }});
            JSONObject dashboard = null;
            if (unitResponseObject.succeeded() && unitResponseObject.dataToJson() != null)
                dashboard = unitResponseObject.dataToJson();
            if (dashboard == null || !dashboard.containsKey("dashboard")) {
                dashboard.clear();

                JSONObject _dashboard = new JSONObject();
                _dashboard.put("id", null);
                _dashboard.put("title", slug);
                JSONArray tags = new JSONArray();
                tags.add(EnvUtil.getShortEnvName());
                _dashboard.put("tags", tags);
                _dashboard.put("timezone", "browser");
                dashboard.put("dashboard", _dashboard);
            }

            //  Last N hours
            JSONObject time = new JSONObject();
            time.put("from", "now-1h");
            time.put("to", "now");
            dashboard.getJSONObject("dashboard").put("time", time);

            JSONArray rows = new JSONArray();
            if (dashboard.containsKey("dashboard") && dashboard.getJSONObject("dashboard").containsKey("rows"))
                rows = dashboard.getJSONObject("dashboard").getJSONArray("rows");

            JSONArray new_rows = new JSONArray();

            for (Map.Entry<String, List<OpenFalconBean>> entry : group_dashboard.getValue().entrySet()) {
                String title = entry.getKey();
                List<OpenFalconBean> openFalconBeans = entry.getValue();

                boolean isExists = false;
                for (int i = 0; rows != null && i < rows.size(); i++) {
                    JSONObject row = rows.getJSONObject(i);
                    JSONArray panels = row.getJSONArray("panels");
                    for (int k = 0; k < panels.size(); k++) {
                        JSONObject panel = panels.getJSONObject(k);
                        if (panel.getString("title") != null && panel.getString("title").equals(title)) {
                            isExists = true;

                            JSONArray _targets = new JSONArray();
                            JSONArray targets = panel.getJSONArray("targets");
                            openFalconBeans.stream().forEach(openFalconBean -> {
                                int j = 0;
                                for (; j < targets.size(); j++) {
                                    JSONObject target = targets.getJSONObject(j);
                                    if (target.getString("target").equals(openFalconBean.grafanaMetric())) {
                                        _targets.add(new JSONObject() {{
                                            put("target", openFalconBean.grafanaMetric());
                                        }});
                                        break;
                                    }
                                }
                                if (j >= targets.size())
                                    _targets.add(new JSONObject() {{
                                        put("target", openFalconBean.grafanaMetric());
                                    }});
                            });

                            for (int j = 0; j < _targets.size(); j++) {
                                if (!_targets.getJSONObject(j).containsKey("refId"))
                                    _targets.getJSONObject(j).put("refId", REF_ID.get(j));
                            }

                            panel.put("targets", _targets);

                            break;
                        }
                    }
                }
                if (isExists == false) {
                    JSONObject row = new JSONObject();
                    JSONArray panels = new JSONArray();
                    JSONObject panel = new JSONObject();
                    panel.put("renderer", "flot");
                    panel.put("stack", false);
                    panel.put("type", "graph");
                    panel.put("percentage", false);
                    panel.put("dashLength", 10);
                    panel.put("lines", true);
                    panel.put("spaceLength", 10);
                    panel.put("nullPointMode", null);
                    panel.put("steppedLine", false);
                    panel.put("fill", 1);
                    panel.put("linewidth", 1);
                    panel.put("bars", false);
                    panel.put("dashes", false);
                    panel.put("pointradius", 5);
                    panel.put("span", 12);
                    panel.put("title", title);
                    JSONArray _targets = new JSONArray();
                    openFalconBeans.stream().forEach(openFalconBean -> {
                        _targets.add(new JSONObject() {{
                            put("target", openFalconBean.grafanaMetric());
                        }});
                    });
                    for (int j = 0; j < _targets.size(); j++) {
                        if (!_targets.getJSONObject(j).containsKey("refId"))
                            _targets.getJSONObject(j).put("refId", REF_ID.get(j));
                    }
                    panel.put("targets", _targets);
                    panels.add(panel);
                    row.put("panels", panels);

                    new_rows.add(row);
                }
            }

            if (!new_rows.isEmpty())
                rows.addAll(new_rows);

            JSONObject request = new JSONObject();
            JSONObject _dashboard = new JSONObject();
            _dashboard.put("id", dashboard.getJSONObject("dashboard").getOrDefault("id", null));
            _dashboard.put("title", dashboard.getJSONObject("dashboard").getString("title"));
            _dashboard.put("tags", dashboard.getJSONObject("dashboard").getJSONArray("tags"));
            _dashboard.put("time", dashboard.getJSONObject("dashboard").getJSONObject("time"));
            _dashboard.put("timezone", dashboard.getJSONObject("dashboard").getString("timezone"));
            _dashboard.put("rows", rows);
            request.put("dashboard", _dashboard);
            request.put("overwrite", true);

            dashboards.put(slug, request);

//            LOG.info(String.format("dashboard: %s, JSON 数据: %s", slug, request.toJSONString()));
        }

        // Link Dashboards 导航栏
        final String NAVIGATION_BAR_TITLE = "Link Dashboards";
        JSONObject mainDashboard = dashboards.get(DEFAULT_DASHBOARD);

//        LOG.info(String.format("dashboard.size: %s, 主面板: %s", dashboards.size(), mainDashboard != null ? mainDashboard.toJSONString() : "没有"));

        if (mainDashboard != null && mainDashboard.containsKey("dashboard") && mainDashboard.getJSONObject("dashboard").containsKey("rows")) {
            JSONArray rows = mainDashboard.getJSONObject("dashboard").getJSONArray("rows");

//            LOG.info(String.format("主面板: %s, rows.size: %s", mainDashboard != null ? mainDashboard.toJSONString() : "没有", rows.size()));

            JSONObject navigationBar = null;
            if (dashboards.size() > 1) // 如果只有一个 dashboard 是没有必要创建 导航栏 的...
            {
                // 找到并移除之前的 导航栏
                Iterator iterator = rows.iterator();
                while (iterator.hasNext()) {
                    JSONObject row = (JSONObject) iterator.next();
                    if (NAVIGATION_BAR_TITLE.equals(row.getString("title"))) {
                        navigationBar = row;
                        iterator.remove();
                        break;
                    }
                }

//                LOG.info(String.format("导航栏: %s", navigationBar != null ? navigationBar : "没有"));

                if (navigationBar == null) {
                    navigationBar = new JSONObject();
                    navigationBar.put("collapse", false);
                    navigationBar.put("height", 46 * (((dashboards.size() - 1) > 0) ? (dashboards.size() - 1) : 1));
                    navigationBar.put("repeat", null);
                    navigationBar.put("repeatIteration", null);
                    navigationBar.put("repeatRowId", null);
                    navigationBar.put("showTitle", true);
                    navigationBar.put("title", NAVIGATION_BAR_TITLE);
                    navigationBar.put("titleSize", "h4");
                }

                JSONArray panels = new JSONArray();
                JSONObject panel = new JSONObject();
                panel.put("editable", true);
                panel.put("error", false);
                panel.put("headings", false);
                panel.put("limit", 10);
                panel.put("links", new JSONArray());
                panel.put("query", "");
                panel.put("recent", false);
                panel.put("search", true);
                panel.put("span", 4);
                panel.put("starred", false);
                panel.put("tags", EnvUtil.getShortEnvName());
                panel.put("showTitle", false);
                panel.put("title", EnvUtil.getShortEnvName() + " Dashboards");
                panel.put("transparent", true);
                panel.put("type", "dashlist");
                panels.add(panel);

                navigationBar.put("panels", panels);

//                LOG.info(String.format("导航栏: %s", navigationBar.toJSONString()));
            }

//            LOG.info(String.format("dashboard.size: %s, 主面板: %s, rows.size: %s, 导航栏: %s", dashboards.size(), mainDashboard.getString("title"), rows.size(), navigationBar != null ? navigationBar.toJSONString() : "无须导航栏"));

            if (navigationBar != null)
                rows.add(0, navigationBar); // 将 导航栏 放置在 N 行
        }

        for (Map.Entry<String, JSONObject> entry : dashboards.entrySet()) {
//            LOG.info("Grafana Dashboard 数据: " + entry.getValue().toJSONString());

            UnitResponse _unitResponseObject = SyncXian.call("grafanaService", "dashboardCreateUpdate", entry.getValue());
            LOG.info("grafanan create/update: " + _unitResponseObject);
        }

        long endNanoTime = System.nanoTime();
        LOG.info(String.format("Grafana 处理 Dashboard %s 个, 耗时 %s", dashboards.size(), (endNanoTime - startNanoTime) / 1000000));
    }

}
