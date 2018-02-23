package info.xiancloud.plugin.distribution;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.message.RequestContext;

/**
 * unit message type.
 *
 * @author happyyangyuan
 */
public enum MessageType {

    offline,//node offline
    request,//unit request message
    response,//unit response message
    requestStream,//rpc request with stream
    responseStream,//rpc response with stream
    ping,// rpc ping
    ;

    public static final String CONTEXT_KEY = "context";
    public static final String TYPE_KEY = "messageType";

    public static MessageType getMessageType(JSONObject msgJson) {
        return msgJson.getJSONObject(CONTEXT_KEY).getObject(TYPE_KEY, MessageType.class);
    }

    public static boolean isStream(JSONObject msgJson) {
        switch (getMessageType(msgJson)) {
            case requestStream:
            case responseStream:
                return true;
            default:
                return false;
        }
    }

    public static boolean isRequestStream(JSONObject msgJson) {
        return getMessageType(msgJson) == requestStream;
    }

    public static boolean isResponseStream(JSONObject msgJson) {
        return getMessageType(msgJson) == responseStream;
    }

    public static boolean isDefaultRequest(JSONObject msgJson) {
        return getMessageType(msgJson) == request;
    }

    public static boolean isDefaultResponse(JSONObject msgJson) {
        return getMessageType(msgJson) == response;
    }

    public static boolean isPing(JSONObject msgJson) {
        return getMessageType(msgJson) == ping;
    }

    public static final String PING_MSG = new JSONObject() {{
        put(CONTEXT_KEY, RequestContext.create().setMessageType(MessageType.ping));
    }}.toJSONString();
}
