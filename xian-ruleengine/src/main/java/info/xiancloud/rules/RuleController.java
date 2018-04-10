package info.xiancloud.rules;

import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.gateway.controller.BaseController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态规则脚本的父类
 * <p>
 * Rule controller
 *
 * @author happyyangyuan
 */
public abstract class RuleController extends BaseController {
    protected final static String END = "end";
    public final static Map<Class, Map<String, Method>> rules_methods = new ConcurrentHashMap<>();
    public final static Map<Class, Map<String, String>> rules_params = new ConcurrentHashMap<>();
    public final static Map<Class, Map<String, String>> rules_returnParams = new ConcurrentHashMap<>();
    public final static Map<Class, String> rules_first = new ConcurrentHashMap<>();

    protected UnitResponse unitResponse;
    protected String next;
    private final Map<String, Object> resultMap = new HashMap<>();

    public RuleController() {
        next = getFirst();
    }

    protected void atomicAsyncRun() {
        work();
    }

    protected abstract void callNext();

    protected abstract String getParams();

    protected abstract String getReturnParams();

    protected abstract String getFirst();

    //不要手贱降低访问等级！
    protected boolean isTransactional() {
        return false;
    }

    private void end() {
        if (unitResponse.succeeded()) {
            unitResponse.setData(putIfNotExist(unitResponse.getData(), resultMap));
        }
        handler.callback(unitResponse);
    }

    //执行
    private void work() {
        if (next.equals(END)) {
            end();
        } else {
            Map<String, Object> params = processParams();
            LOG.info("[RuleController] params = " + params);
            SingleRxXian
                    .call(next.split("_")[0], next.split("_")[1], params)
                    .subscribe(unitResponse1 -> {
                        unitResponse = unitResponse1;
                        processReturnParams();
                        callNext();
                        work();
                    });
        }
    }

    private Map<String, Object> processParams() {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("$msgId", originMap.get("$msgId"));
            put("$header", originMap.get("$header"));
        }};
        String[] splitParams = getParams().split(",");
        for (String splitParam : splitParams) {
            if (splitParam.contains("=")) {
                String[] str = splitParam.split("=");
                int value = 0;
                try {
                    value = Integer.valueOf(str[1]);//TODO 它只支持规则内配置入参是int类型的特定值
                } catch (Exception e) {
                    LOG.error(e);
                }
                originMap.put(str[0], value);
                map.put(str[0], originMap.get(str[0]));
            } else if (splitParam.contains("->")) {
                //换名字
                String[] str = splitParam.split("->");
                originMap.put(str[1], originMap.get(str[0]));//过渡接口参数名称
                map.put(str[1], originMap.get(str[1]));//过渡后的参数和参数值还会被保存在originMap内的
            } else {
                map.put(splitParam, originMap.get(splitParam));
            }
        }
        return map;
    }

    private void processReturnParams() {
        if (unitResponse.succeeded()) {
            String[] splitReturnParams = getReturnParams().split(",");
            for (String splitReturnParam : splitReturnParams) {
                if (splitReturnParam.contains("->")) {
                    //换名字
                    String[] str = splitReturnParam.split("->");
                    originMap.put(str[1], unitResponse.value(0, str[0]));
                    resultMap.put(str[1], originMap.get(str[1]));
                } else {
                    originMap.put(splitReturnParam, unitResponse.value(0, splitReturnParam));
                    resultMap.put(splitReturnParam, originMap.get(splitReturnParam));
                }
            }
        }
    }

    /**
     * 如果最终返回结果内缺那个属性,那么就放进去,如果不缺,那么什么也不做.只针对整个流程都返回成功码的场景.
     */
    @SuppressWarnings("unchecked")
    private static Object putIfNotExist(Object data, Map<String, Object> resultMap) {
        LOG.info("[RuleController]补全信息时:data = " + data);
        if (data == null) {
            return resultMap;
        } else if (!(data instanceof Collection) && !(data instanceof Map)) {//既不是map也不是list,需要显示地配置出最后一个unit的返回结果列表
            return resultMap;
        } else if (data instanceof Map) {
            putToMap((Map) data, resultMap);
            return data;
        } else/* if (data instanceof Collection)*/ {
            Collection newCollection = new ArrayList<>();
            Collection listData = (Collection) data;
            for (Object o : listData) {
                newCollection.add(putIfNotExist(o, resultMap));
            }
            return newCollection;
        }
    }

    @SuppressWarnings("unchecked")
    private static void putToMap(Map mapData, Map<String, Object> resultMap) {
        for (String key : resultMap.keySet()) {
            mapData.putIfAbsent(key, resultMap.get(key));
        }
    }
}


