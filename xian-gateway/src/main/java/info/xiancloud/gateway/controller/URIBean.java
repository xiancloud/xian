package info.xiancloud.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.LOG;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * URI格式为：/group/unit/extension?queryString
 * 其中，"/group/unit" 为 {@link #getBasePath basePath}；
 * <p>
 * "extension" 为 {@link #uriExtension}
 * queryString 为 {@link #uriParameters}
 *
 * @author happyyangyuan
 */
public class URIBean {
    private String group;
    private String unit;
    private String uriExtension;
    private final JSONObject uriParameters = new JSONObject();

    public static URIBean create(String uri) {
        return new URIBean(uri);
    }

    /**
     * Check the uri is xian pattern or not.
     * xian pattern uri must starts with /${group}/${unit}
     * TODO combine URI checking with UriBean creation. And use UriBean reference instead of URI string later on for performance consideration.
     *
     * @param uri the URI to be checked
     * @return true if it is xian pattern false other wise.
     */
    public static boolean checkUri(String uri) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri, true);
        String path = queryStringDecoder.path();
        int groupIndex = path.indexOf('/') + 1,
                unitIndex = path.indexOf('/', groupIndex) + 1;
        if (groupIndex == 0 || unitIndex == 0) {
            LOG.warn("URI is illegal: " + uri);
            return false;
        }
        return true;
    }

    private URIBean(String uri) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri, true);
        String path = queryStringDecoder.path();
        int groupIndex = path.indexOf('/') + 1,
                unitIndex = path.indexOf('/', groupIndex) + 1,
                uriExtensionIndex = path.indexOf('/', unitIndex) + 1;
        if (groupIndex == 0 || unitIndex == 0) {
            throw new IllegalArgumentException("URI is illegal: " + uri);
        }
        group = path.substring(groupIndex, unitIndex - 1);
        if (uriExtensionIndex == 0) {
            unit = path.substring(unitIndex);
        } else {
            unit = path.substring(unitIndex, uriExtensionIndex - 1);
            uriExtension = path.substring(uriExtensionIndex);
        }
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        parameters.forEach((key, values) -> {
            if (values == null || values.isEmpty()) {
                //空参数统一对应空字符串
                uriParameters.put(key, "");
            } else if (values.size() == 1) {
                uriParameters.put(key, values.get(0));
            } else {
                uriParameters.put(key, values);
            }
        });
    }


    public String getGroup() {
        return group;
    }

    public String getUnit() {
        return unit;
    }

    public String getUriExtension() {
        return uriExtension;
    }


    public JSONObject getUriParameters() {
        return uriParameters;
    }


    /**
     * 返回uri内的base path，格式：eg. /group/unit
     */
    public String getBasePath() {
        return "/".concat(group).concat("/").concat(unit);
    }
}
