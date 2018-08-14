package info.xiancloud.core.distribution;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Constant;
import info.xiancloud.core.Group;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.IdManager;
import info.xiancloud.core.message.RequestContext;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.id.NodeIdBean;
import info.xiancloud.core.message.sender.remote.msg_publisher.IMsgPublisher;
import info.xiancloud.core.stream.StreamManager;
import info.xiancloud.core.stream.StreamSerializer;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.ThreadUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static info.xiancloud.core.distribution.LocalNodeManager.handleMap;

/**
 * The abstraction of an application instance, it holds some node information and the action of msg sending/responding.
 * <p>
 * ps. Currently this class is not serializable. We only serialize the {@link NodeStatus} object.
 *
 * @author happyyangyuan
 */
public class Node implements INode {

    private final String nodeId;
    private final String application;
    private final Date initDate;
    private final IMsgPublisher publisher = IMsgPublisher.defaultPublisher;
    public static int RPC_PORT;

    static {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            RPC_PORT = socket.getLocalPort();
        } catch (Throwable e) {
            LOG.error(e);
            System.exit(Constant.SYSTEM_EXIT_CODE_FOR_RPC_ERROR);
        }
    }

    protected Node(String nodeId) {
        this.nodeId = nodeId;
        this.application = NodeIdBean.parse(nodeId).getApplication();
        this.initDate = new Date();
    }

    @Override
    public void init() {
        LOG.debug("nothing to do.");
    }

    public String getNodeId() {
        return nodeId;
    }

    public void send(UnitRequest request, NotifyHandler handler) {
        String ssid = IdManager.nextSsid();
        request.getContext().setSsid(ssid);
        fillRequestContext(request.getContext());
        String payload = JSON.toJSONStringWithDateFormat(request, Constant.DATE_SERIALIZE_FORMAT);
        handleMap.put(ssid, handler);
        publisher.p2pPublish(request.getContext().getDestinationNodeId(), payload);
    }

    public UnitResponse send(UnitRequest request) {
        String ssid = IdManager.nextSsid();
        request.getContext().setSsid(ssid);
        fillRequestContext(request.getContext());
        UnitResponse unitResponse = UnitResponse.create(true);
        final CountDownLatch latch = new CountDownLatch(1);
        NotifyHandler handler = new NotifyHandler() {
            public void handle(UnitResponse output) {
                UnitResponse.copy(output, output);
                latch.countDown();
            }
        };
        handleMap.put(ssid, handler);
        String payload = JSON.toJSONStringWithDateFormat(request, Constant.DATE_SERIALIZE_FORMAT);
        publisher.p2pPublish(request.getContext().getDestinationNodeId(), payload);
        try {
            if (!latch.await(Constant.UNIT_DEFAULT_TIME_OUT_IN_MILLI, TimeUnit.MILLISECONDS)) {
                handler.setTimeout(true);
                return UnitResponse.createError(Group.CODE_TIME_OUT, null, "Response time out!")
                        .setContext(UnitResponse.Context.create().setSsid(ssid));
            }
        } catch (InterruptedException e) {
            return UnitResponse.createException(e);
        }
        return unitResponse;
    }

    public void sendBack(UnitResponse unitResponse, Consumer<String> onFailure) {
        InputStream inputStream = null;
        if (unitResponse.getData() != null && unitResponse.getData() instanceof File) {
            try {
                inputStream = new FileInputStream(unitResponse.dataToType(File.class));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("文件 " + unitResponse.dataToType(File.class).getName() + " 不存在", e);
            }
        } else if (unitResponse.getData() != null && unitResponse.getData() instanceof InputStream) {
            inputStream = unitResponse.dataToType(InputStream.class);
        }
        if (inputStream != null) {
            StreamSerializer.singleton.encodeAndApply(inputStream, unitResponse.getContext().getSsid(), unitResponse.getMsgId(), streamBean -> {
                //停服务时，作为server，如果发送流的线程任务销毁超时，那就没办法了，谁叫你在发送流时停服务的？
                publisher.p2pPublish(unitResponse.getContext().getDestinationNodeId(), JSON.toJSONString(streamBean)/*.concat(Constant.RPC_DELIMITER) 底层的rpc已经帮我们增加了delimiter了，所以这里不要再增加！*/);
                JSONObject logJson = new JSONObject();
                logJson.put("type", "stream");
                logJson.put("detail", "sent ---------------->    " + (streamBean.getHeader().getIndex() + 1) * StreamManager.BUF_SIZE_IN_BYTE / 1024d + " kb");
                logJson.put("header", streamBean.getHeader());
                LOG.info(logJson);
            });
        } else {
            fillResponseContext(unitResponse.getContext());
            String payload = JSON.toJSONStringWithDateFormat(unitResponse, Constant.DATE_SERIALIZE_FORMAT);
            if (!publisher.p2pPublish(unitResponse.getContext().getDestinationNodeId(), payload)) {
                LOG.info("反向发送响应结果失败，因此做回调处理");
                if (onFailure != null)
                    onFailure.accept(payload);
                else
                    LOG.error(new JSONObject() {{
                        put("type", "sendBackFailure");
                        put("description", "消息回送失败！！！");
                        put("ssid", unitResponse.getContext().getSsid());
                        put("destinationNodeId", unitResponse.getContext().getDestinationNodeId());
                    }});
            }
            /*
                publisher.p2pPublish(targetId, payload);
                @2017-07 由于不能统一升级所有节点框架版本，因此这种方式不合理，未升级框架的节点的rpcServer没有能力处理response类型消息
                @2017-09 这个服务注册重构的版本会升级所有节点框架版本，现统一sendBack方式为server端反转为rpcClient身份将消息返回
            */
        }
    }

    public void sendBack(UnitResponse unitResponseObject) {
        sendBack(unitResponseObject, null);
    }

    //1. init a msgId if not exists one, set msgId into the context.
    //2. set node status into the context.
    private void fillRequestContext(RequestContext context) {
        IdManager.makeSureMsgId(context);
        context.setNodeStatus(getSimpleStatus());
        context.setMessageType(MessageType.request);
        context.setSourceNodeId(nodeId);//this node id
    }

    //1. init a msgId if not exists one, set msgId into the context.
    //2. set node status into the context.
    private void fillResponseContext(UnitResponse.Context responseContext) {
        IdManager.makeSureMsgId(responseContext);
        responseContext.setNodeStatus(getSimpleStatus());
        responseContext.setMessageType(MessageType.response);
    }

    /**
     * 生成一个服务注册json对象
     */
    public NodeStatus getFullStatus() {
        NodeStatus nodeStatus = getSimpleStatus();
        LocalUnitsManager.searchUnitMap(searchUnitMap -> nodeStatus.setUnits(searchUnitMap.keySet()));
        nodeStatus.setPort(RPC_PORT);
        nodeStatus.setHost(EnvUtil.getLocalIp());
        return nodeStatus;
    }

    @Override
    public NodeStatus getSimpleStatus() {
        return NodeStatus.create().setNodeId(nodeId)
                .setQueueSize(ThreadPoolManager.queueSize())
                .setActiveCount(ThreadPoolManager.activeCount())
                .setCpuCores(ThreadUtils.CPU_CORES)
                .setInitTime(initDate.getTime());
    }

    public void destroy() {
        //nothing need to do.
    }

    public String application() {
        return application;
    }

}
