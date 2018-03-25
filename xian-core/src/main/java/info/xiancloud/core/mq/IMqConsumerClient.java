package info.xiancloud.core.mq;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.Reflection;

import java.util.function.Function;

/**
 * @author happyyangyuan
 * 仅供内部使用
 */
public interface IMqConsumerClient extends IMqClient {

    IMqConsumerClient singleton = Reflection.getSubClassInstances(IMqConsumerClient.class).isEmpty() ? null : Reflection.getSubClassInstances(IMqConsumerClient.class).get(0);

    /*static IMqConsumerClient singleton() {
        for (IMqClient singleton : singletons) {
            if (singleton instanceof IMqConsumerClient)
                return (IMqConsumerClient) singleton;
        }
        throw new RuntimeException("找不到mqPublisher客户端");
    }*/

    /**
     * 订阅动态队列，当队列无人关注时会自动删除
     *
     * @param queueName
     * @param function
     * @return 成功返回true
     */
    boolean consumeNonStaticQueue(String queueName, Function<JSONObject, Boolean> function);

    /**
     * 订阅静态队列
     *
     * @param queueName
     * @param function
     * @return 订阅成功返回true
     */
    boolean consumeStaticQueue(String queueName, Function<JSONObject, Boolean> function);

    /**
     * @param queueName
     * @return 取订成功返回true
     */
    boolean unconsume(String queueName);

    /**
     * @param queueName
     * @param function
     * @return 拉取消息成功返回true
     */
    boolean pullStaticQueue(String queueName, Function<JSONObject, Boolean> function);

    /**
     * @param queueName
     * @param function
     * @return 拉取成功返回true
     */
    boolean pullNonStaticQueue(String queueName, Function<JSONObject, Boolean> function);

}
