package info.xiancloud.plugin.utils;

import info.xiancloud.plugin.conf.XianConfig;

import java.util.HashMap;
import java.util.Map;

public class GrafanaUtil
{

    private static final Map<String, String> HTTP_HEADERS = new HashMap<String, String>(){{
        put("Accept", "application/json");
        put("Content-Type", "application/json");
        put("Access-Control-Allow-Origin", "*");
    }};

    public static Map<String, String> gainHttpHeaders ()
    {
        Map<String, String> headers = new HashMap<>();

        headers.putAll(HTTP_HEADERS);

        headers.put("Authorization", XianConfig.get("grafana_api_token"));

        return headers;
    }

}
