package info.xiancloud.core.message.sender.remote.msg_publisher;

/**
 * 跨节点消息发送接口；单例模式
 *
 * @author happyyangyuan
 */
public interface IMsgPublisher {

    /**
     * 单例的默认发布器
     */
    IMsgPublisher defaultPublisher = new DefaultMsgPublisher();

    /**
     * 点对点消息发送
     *
     * @param nodeId  接收方节点id
     * @param payload 消息体
     * @return 成功或者失败
     */
    boolean p2pPublish(String nodeId, String payload);

}
