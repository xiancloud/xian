package info.xiancloud.core.mq;

import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.Reflection;

/**
 * mq消息发送客户端，仅供内部使用
 *
 * @author happyyangyuan
 */
public interface IMqPubClient extends IMqClient {

    //既支持订阅和发布的客户端是同一个子类情况 也支持分开子类的情况
    IMqPubClient singleton = IMqConsumerClient.singleton != null && IMqConsumerClient.singleton instanceof IMqPubClient ?
            (IMqPubClient) IMqConsumerClient.singleton
            : Reflection.getSubClassInstances(IMqPubClient.class).isEmpty() ? null : Reflection.getSubClassInstances(IMqPubClient.class).get(0);

    /*static IMqPubClient singleton() {
        for (IMqClient singleton : singletons) {
            if (singleton instanceof IMqPubClient)
                return (IMqPubClient) singleton;
        }
        throw new RuntimeException("找不到mqPublisher客户端");
    }*/

    /**
     * 注意子类必须实现为并发安全的单例模式
     *
     * @param queueName
     * @param payload
     * @return true 成功；false 失败。
     */
    boolean p2pPublish(String queueName, String payload);

    /**
     * 发布到静态队列
     *
     * @param queueName
     * @param payload
     * @return true成功，false失败。
     */
    boolean staticPublish(String queueName, String payload);

}
