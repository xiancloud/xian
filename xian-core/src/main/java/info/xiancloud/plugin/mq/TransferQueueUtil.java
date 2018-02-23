package info.xiancloud.plugin.mq;

import info.xiancloud.plugin.message.IdManager;
import info.xiancloud.plugin.util.LOG;

/**
 * A transfer message queue is to hold messages when the destination transferable unit is offline, so that no messages will
 * be lost. You can configure a unit as transferable by set the meta attribute 'transferable' to true.
 * Currently we use rabbitmq as the mq provider.
 * Transfer queue names is combined with groupName and a fixed end fix, eg. groupName_transfer
 *
 * @author happyyangyuan
 */
public class TransferQueueUtil {

    /**
     * 获取name对应的中转静态队列名
     */
    public static String getTransferQueue(String name) {
        LOG.debug("对于需要保序的情况，只应该启动单个中转节点，目前启动多个节点原节点会被挤掉线,但是由于重连机制，会轮着挤掉线");
        return IdManager.generateStaticQueueId(buildTransferName(name));
    }

    /**
     * Currently transfer name is combined by group name and '_transfer' end fix, eg. groupName_transfer
     *
     * @param name current the group name.
     * @return application_transfer
     */
    private static String buildTransferName(String name) {
        return name.concat(TRANSFER_END_FIX);
    }

    private static final String TRANSFER_END_FIX = "_transfer";

    /**
     * 检查对列是否是一个中转队列
     *
     * @param queueName 队列全名
     */
    public static boolean isTransferQueue(String queueName) {
        return IdManager.getSimpleNameFromStaticQueue(queueName).endsWith(TRANSFER_END_FIX);
    }
}
