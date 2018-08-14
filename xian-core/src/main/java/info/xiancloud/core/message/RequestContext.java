package info.xiancloud.core.message;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Constant;
import info.xiancloud.core.distribution.MessageType;
import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.core.support.authen.AccessToken;

import java.util.Map;

/**
 * message context while communicating between units.
 *
 * @author happyyangyuan
 */
@SuppressWarnings("ALL")
public final class RequestContext {

    private String ssid;// unit request id, TODO no matter local or remote unit request.
    private String msgId;// the business chain id.
    private NodeStatus nodeStatus;
    /*private long creationTimestamp; timestamp for message creation; we disable this property for performance consideration.  */
    private long sentTimestamp;//message sent time in milli
    private MessageType messageType;
    private Map<String, String> header;
    private String uri;
    private String body;
    private JSONObject uriParameters;// uri query string decoded parameters
    private String uriExtension;// except for '/v1.0/group/unit', other part of uri is the uirExtension.
    private boolean transferredAlready = false;
    private boolean readyOnly = false;//whether query data from read-only database.
    private boolean fromRemote = false;//whether message is from remote node.
    private boolean routed = false;//whether this message is a targeting one.
    private AccessToken accessToken;// api access token from api gateway which represents the third application.
    private String group;// group this request is for.
    private String unit;//unit this request is for, this is the concrete unit.
    private String virtualUnit; // virtual unit name, if the unit is no virtual then virtual unit name is same as the unit name.
    private String destinationNodeId;//the destination node this request is meant to be sent
    private String sourceNodeId;// which node this request is from
    private String ip;//which ip this request is from. present only when this request is from the http gateway.
    private long timeOutInMilli = Constant.UNIT_DEFAULT_TIME_OUT_IN_MILLI;//the timeout in milliseconds waiting for the execution of the destiniation unit.

    public String getDestinationNodeId() {
        return destinationNodeId;
    }

    public RequestContext setDestinationNodeId(String destinationNodeId) {
        this.destinationNodeId = destinationNodeId;
        return this;
    }

    public String getSourceNodeId() {
        return sourceNodeId;
    }

    public RequestContext setSourceNodeId(String sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
        return this;
    }

    public String getVirtualUnit() {
        return virtualUnit;
    }

    public RequestContext setVirtualUnit(String virtualUnit) {
        this.virtualUnit = virtualUnit;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public RequestContext setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public RequestContext setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    /**
     * Only UnitRequest in api gateway contains AccessToken value. For others null is returned.
     *
     * @return {@link AccessToken}
     */
    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }


    public long getSentTimestamp() {
        return sentTimestamp;
    }

    public RequestContext setSentTimestamp(long sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
        return this;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public RequestContext setMessageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    public boolean isTransferredAlready() {
        return transferredAlready;
    }

    public RequestContext setTransferredAlready(boolean transferredAlready) {
        this.transferredAlready = transferredAlready;
        return this;
    }

    public String getSsid() {
        return ssid;
    }

    public RequestContext setSsid(String ssid) {
        this.ssid = ssid;
        return this;
    }

    public String getMsgId() {
        return msgId;
    }

    public RequestContext setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public RequestContext setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
        return this;
    }

    /*public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public RequestContext setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }*/

    /**
     * If this unitRequest is a http request from the api gateway, then an non-empty http header map is returned,
     * Note that, this returned header map only contains xian framework's internal header attributes, eg. header attributes started with {@linkplain Constant#XIAN_HEADER_PREFIX "Xian-"}
     */
    public Map<String, String> getHeader() {
        return header;
    }

    public RequestContext setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public RequestContext setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getBody() {
        return body;
    }

    public RequestContext setBody(String body) {
        this.body = body;
        return this;
    }

    public JSONObject getUriParameters() {
        return uriParameters;
    }

    public RequestContext setUriParameters(JSONObject uriParameters) {
        this.uriParameters = uriParameters;
        return this;
    }

    public String getUriExtension() {
        return uriExtension;
    }

    public RequestContext setUriExtension(String uriExtension) {
        this.uriExtension = uriExtension;
        return this;
    }

    public boolean isReadyOnly() {
        return readyOnly;
    }

    public RequestContext setReadyOnly(boolean readyOnly) {
        this.readyOnly = readyOnly;
        return this;
    }

    public boolean isFromRemote() {
        return fromRemote;
    }

    public RequestContext setFromRemote(boolean fromRemote) {
        this.fromRemote = fromRemote;
        return this;
    }

    public boolean isRouted() {
        return routed;
    }

    public RequestContext setRouted(boolean routed) {
        this.routed = routed;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public RequestContext setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public long getTimeOutInMilli() {
        return timeOutInMilli;
    }

    /**
     * set the timeout for the execution of the requested unit.
     * defaults to {@link Constant#UNIT_DEFAULT_TIME_OUT_IN_MILLI} if you leave it alone.
     *
     * @param timeOutInMilli
     */
    public void setTimeOutInMilli(long timeOutInMilli) {
        this.timeOutInMilli = timeOutInMilli;
    }

    public static RequestContext create() {
        return new RequestContext();
    }
}
