package info.xiancloud.mq.backed;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Input;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.mq.TransferQueueUtil;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * mqtt queue piling count monitoring
 *
 * @deprecated we don't use mqtt queue currently.
 */
public class MqttQueuePiledUpMonitor {

    /**
     * rabbitmq vhost
     */
    public static final String VHOST = "/";

    public Input getInputObjs() {
        return new Input();
    }

    public List<String> getErrorCodes() {
        return null;
    }

    public UnitResponse execute(UnitRequest msg) {
        JSONObject params = new JSONObject();
        params.put("title", "堆积情况");
        params.put("value", -1);
        try {
            int totalLen = 0;
            List<String> mgmtCloudClients = new ArrayList<>();
            mgmtCloudClients.add(TransferQueueUtil.getTransferQueue("xian_management_cloud"));
            for (String clientId : mgmtCloudClients) {
                int len = getLen(clientId);
                LOG.info(String.format("[堆积统计] %s 已经堆积了: %s 条 消息.", clientId, len));
                totalLen += len;
            }
            params.put("value", totalLen);
            return UnitResponse.createSuccess(params);
        } catch (Throwable e) {
            return UnitResponse.createException(e);
        }
    }

    private String getRabbitMqApiUrl(String queueName) {
        String tcpUrl, apiUrl;
        if (EnvUtil.isQcloudLan()) {
            tcpUrl = XianConfig.getStringArray("rabbitmqLanServerUrls")[0];
        } else {
            tcpUrl = XianConfig.getStringArray("rabbitmqInternetServerUrls")[0];
        }
        try {
            URL url = new URL(tcpUrl.replaceFirst("tcp", "http"));//懒得弄就这样
            apiUrl = "http://" + url.getHost() + ":15672/api/queues/" + URLEncoder.encode(VHOST, "utf-8") + "/" + URLEncoder.encode(queueName, "utf-8");
        } catch (MalformedURLException e) {
            throw new RuntimeException("config.txt内配置mqtt.*.serverURIs配置值格式错误,它不是标准的url格式 :" + tcpUrl, e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        LOG.info("[RabbitMQ]  接口地址apiUrl = " + apiUrl);
        return apiUrl;
    }

    private JSONObject getQueueJSONObject(String clientId) {
        String queueName = getQueueName(clientId);
        String url = getRabbitMqApiUrl(queueName);
        String res = SyncXian.call("httpClient", "basicAuthApacheHttpClientGet", new JSONObject() {{
            put("url", url);
            put("userName", XianConfig.get("rabbitmqUserName"));
            put("password", XianConfig.get("rabbitmqPwd"));
        }}).dataToJson().getString("entity");
        LOG.info(String.format("[RabbitMQ]  队列%s信息:%s", queueName, res));
        return JSONObject.parseObject(res);
    }

    /**
     * 按照rabbitmq的api协议构造对列名
     */
    private String getQueueName(String clientId) {
        return "mqtt-subscription-" + clientId + "qos1";
    }

    private int getLen(String clientId) {
        JSONObject queueJSONObject = getQueueJSONObject(clientId);
        return queueJSONObject.getJSONObject("backing_queue_status").getInteger("len");
    }

}
