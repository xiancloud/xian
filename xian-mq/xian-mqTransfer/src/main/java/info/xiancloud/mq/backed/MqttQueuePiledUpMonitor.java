package info.xiancloud.mq.backed;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.mq.TransferQueueUtil;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.ListenableCountDownLatch;
import io.reactivex.Single;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        JSONObject params = new JSONObject();
        params.put("title", "堆积情况");
        params.put("value", -1);
        List<String> mgmtCloudClients = new ArrayList<>();
        mgmtCloudClients.add(TransferQueueUtil.getTransferQueue("xian_management_cloud"));
        AtomicInteger totalLen = new AtomicInteger(0);
        ListenableCountDownLatch countDownLatch = new ListenableCountDownLatch(mgmtCloudClients.size());
        for (String clientId : mgmtCloudClients) {
            getLen(clientId)
                    .subscribe(len -> {
                        LOG.info(String.format("[堆积统计] %s 已经堆积了: %s 条 消息.", clientId, len));
                        totalLen.addAndGet(len);
                        params.put("value", totalLen.get());
                        countDownLatch.countDown();
                    });
        }
        countDownLatch.addListener(counter -> {
            if (counter == 0)
                handler.handle(UnitResponse.createSuccess(params));
        });
    }

    private String getRabbitMqApiUrl(String queueName) {
        String tcpUrl, apiUrl;
        if (EnvUtil.isLan()) {
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

    private Single<JSONObject> getQueueJSONObject(String clientId) {
        String queueName = getQueueName(clientId);
        String url = getRabbitMqApiUrl(queueName);
        return SingleRxXian.call("httpClient", "basicAuthApacheHttpClientGet", new JSONObject() {{
            put("url", url);
            put("userName", XianConfig.get("rabbitmqUserName"));
            put("password", XianConfig.get("rabbitmqPwd"));
        }}).map(unitResponse -> {
            String res = unitResponse.dataToJson().getString("entity");
            LOG.info(String.format("[RabbitMQ]  队列%s信息:%s", queueName, res));
            return JSONObject.parseObject(res);
        });
    }

    /**
     * 按照rabbitmq的api协议构造对列名
     */
    private String getQueueName(String clientId) {
        return "mqtt-subscription-" + clientId + "qos1";
    }

    private Single<Integer> getLen(String clientId) {
        return getQueueJSONObject(clientId)
                .map(queueJSONObject -> queueJSONObject.getJSONObject("backing_queue_status").getInteger("len"));
    }

}
