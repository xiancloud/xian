package info.xiancloud.core.distribution;

import info.xiancloud.core.init.Destroyable;
import info.xiancloud.core.init.Initable;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.NotifyHandler;

import java.util.function.Consumer;

/**
 * @author happyyangyuan
 */
public interface INode extends Initable, Destroyable {

    /**
     * send asynchronsly
     *
     * @param request the message with payload and destination node id
     */
    void send(UnitRequest request, NotifyHandler handler);

    /**
     * send the message and block util finished.
     *
     * @param request the message with payload and destination node id
     * @deprecated 因为阻塞式发送消耗的资源过多, 因此不建议使用!请使用非阻塞式消息,并使用回调方式!
     */
    UnitResponse send(UnitRequest request);


    /**
     * @see #sendBack(UnitResponse, Consumer)
     */
    void sendBack(UnitResponse unitResponseObject);

    /**
     * node1-------send-------node2   then
     * node2-------sendBack---node1
     * send back asynchronously.
     * Ps. We use rpc one-way long connection instead of two-way long connection. Because there is a possiablity that
     * a the response for a too costy asynchronous request can not be returned use the very two-way long connection when
     * connection is regarded as idle and been closed.
     *
     * @param unitResponse the unitResponse object to send back, it contains context to tell the sender where to send back
     *                     this unitResponse object.
     * @param onFailure    消息发送失败的补救发送措施，回调参数为需要回送的消息payload
     */
    void sendBack(UnitResponse unitResponse, Consumer<String> onFailure);

    /**
     * We use this method to generate the full status of current node.
     * This method is used for service registration.
     *
     * @return full status which represents the full definition of this node.
     */
    NodeStatus getFullStatus();

    /**
     * This method is used to transfer the current status to remote dependent nodes.
     *
     * @return the simple status of current node. service registration should not use this status.
     */
    NodeStatus getSimpleStatus();


}
