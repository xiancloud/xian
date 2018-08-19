package info.xiancloud.core.message;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Constant;
import info.xiancloud.core.distribution.MessageType;
import info.xiancloud.core.message.id.NodeIdBean;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.JavaPIDUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * xian 全局id管理器
 * 职责为：生成id，校验id等等
 *
 * @author happyyangyuan
 */
public class IdManager {

    private static final AtomicLong MSG_ID_SEQUENCE = new AtomicLong(0);
    private static final AtomicLong SSID_SEQUENCE = new AtomicLong(0);
    public static final String LOCAL_NODE_ID;
    public static final String CONTEXT_KEY = MessageType.CONTEXT_KEY;
    public static final String MESSAGE_ID_KEY = "msgId";

    static {
        String tmp = null;
        try {
            tmp = generateClientId(EnvUtil.getApplication());
        } catch (Throwable e) {
            e.printStackTrace();
            LOG.error(e);
            System.exit(Constant.SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR);
        } finally {
            //只是为了保证localNodeId为final
            LOCAL_NODE_ID = tmp;
        }
    }

    /**
     * @return LOCAL_NODE_ID + currentTimeMillis + sequence序号
     */
    public static String nextMsgId() {
        return LOCAL_NODE_ID.concat("_") + System.currentTimeMillis() + "_" + MSG_ID_SEQUENCE.incrementAndGet();
    }

    public static String nextSsid() {
        return LOCAL_NODE_ID.concat("_") + System.currentTimeMillis() + "_" + SSID_SEQUENCE.incrementAndGet();
    }

    private static String generateClientId(String application) {
        return new NodeIdBean(EnvUtil.getEnv(), application, JavaPIDUtil.getProcessName()).getClientId();
    }

    /**
     * 生成一个静态队列id
     *
     * @param simpleName 业务名
     */
    public static String generateStaticQueueId(String simpleName) {
        return new NodeIdBean(EnvUtil.getEnv(), simpleName, "static").getClientId();
    }

    /**
     * 解析静态队列名得到队列对应的业务名称
     *
     * @param staticQueueFullName 静态队列全名
     * @return 业务名称
     */
    public static String getSimpleNameFromStaticQueue(String staticQueueFullName) {
        return NodeIdBean.parse(staticQueueFullName).getApplication();
    }

    /**
     * @deprecated 已废弃mq广播
     */
    private static String sysBroadcastId;

    /**
     * 获取各自带环境因子的统一广播id列表
     *
     * @deprecated 已废弃mq广播
     */
    public static String getSysBroadcastId() {
        if (sysBroadcastId == null) {
            sysBroadcastId = EnvUtil.getEnv() + NodeIdBean.splitter + "sys_broadcast" +
                    NodeIdBean.splitter + "xian";//这里临时兼容后台推送，以后去掉不再订阅广播
            LOG.info("广播topic = " + sysBroadcastId);
        }
        return sysBroadcastId;
    }

    /**
     * map与MsgIdHolder内的$msgId同步，如果二者都没有$msgId，那么为他们初始化一个
     *
     * @return newMsgIdGenerated 若初始化了一个新的$msgId，那么返回true
     * @deprecated We use message context to transmit the msg id now, "$" var in map is deprecated.
     */
    public static boolean makeSureMsgId(Map<String, Object> map) {
        boolean newMsgIdGenerated = false;
        if (StringUtil.isEmpty(map.get("$msgId"))) {
            if (MsgIdHolder.get() == null) {
                MsgIdHolder.init();
                newMsgIdGenerated = true;
                LOG.debug("没有提供$msgId,因此这里初始化一个!");
            }
            map.put("$msgId", MsgIdHolder.get());
        } else {
            MsgIdHolder.set(map.get("$msgId").toString());
        }
        return newMsgIdGenerated;
    }

    /**
     * @param context the unit message context.
     * @return true if a new message id is generated, false other wise.
     */
    public static boolean makeSureMsgId(RequestContext context) {
        boolean newMsgIdGenerated = false;
        if (StringUtil.isEmpty(context.getMsgId())) {
            if (MsgIdHolder.get() == null) {
                MsgIdHolder.init();
                newMsgIdGenerated = true;
                //没有提供msgId,因此这里初始化一个
            }
            context.setMsgId(MsgIdHolder.get());
        } else {
            MsgIdHolder.set(context.getMsgId());
        }
        return newMsgIdGenerated;
    }


    /**
     * @param responseContext the unit response context.
     * @return true if a new message id is generated, false other wise.
     */
    public static boolean makeSureMsgId(UnitResponse.Context responseContext) {
        boolean newMsgIdGenerated = false;
        if (StringUtil.isEmpty(responseContext.getMsgId())) {
            if (MsgIdHolder.get() == null) {
                MsgIdHolder.init();
                newMsgIdGenerated = true;
                LOG.debug("没有提供msgId,因此这里初始化一个!");
            }
            responseContext.setMsgId(MsgIdHolder.get());
        } else {
            MsgIdHolder.set(responseContext.getMsgId());
        }
        return newMsgIdGenerated;
    }

    /**
     * @param unitRequestOrResponseJSONObject the UnitRequest/UnitResponse json object.
     * @return true if a new message id is generated, false other wise.
     */
    public static boolean makeSureMsgId(JSONObject unitRequestOrResponseJSONObject) {
        boolean newMsgIdGenerated = false;
        if (StringUtil.isEmpty(unitRequestOrResponseJSONObject.getJSONObject(CONTEXT_KEY).getString(MESSAGE_ID_KEY))) {
            if (MsgIdHolder.get() == null) {
                MsgIdHolder.init();
                newMsgIdGenerated = true;
                LOG.debug("没有提供$msgId,因此这里初始化一个!");
            }
            unitRequestOrResponseJSONObject.getJSONObject(CONTEXT_KEY).put(MESSAGE_ID_KEY, MsgIdHolder.get());
        } else {
            MsgIdHolder.set(unitRequestOrResponseJSONObject.getJSONObject(CONTEXT_KEY).getString(MESSAGE_ID_KEY));
        }
        return newMsgIdGenerated;
    }

    public static boolean isLocalNodeId(String nodeId) {
        return LOCAL_NODE_ID.equals(nodeId);
    }

}
