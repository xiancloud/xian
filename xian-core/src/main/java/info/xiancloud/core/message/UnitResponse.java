package info.xiancloud.core.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import info.xiancloud.core.Constant;
import info.xiancloud.core.Group;
import info.xiancloud.core.Page;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.MessageType;
import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.core.util.CloneUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.file.StreamUtil;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Unified response class for the {@link Unit Xian unit}.
 *
 * @author happyyangyuan
 */
final public class UnitResponse {
    /**
     * the code of this unit response.
     * code {@link Group#CODE_SUCCESS} represents a successful unit response, otherwise failure.
     */
    private String code;
    /**
     * the data object.
     */
    private Object data;
    /**
     * null if no exception. Do not use this exception property when code is {@link Group#CODE_SUCCESS}
     */
    private Throwable exception;
    /**
     * Error message. Do not use this errMsg when code is {@link Group#CODE_SUCCESS}
     */
    private String errMsg;

    /**
     * There is always a default. Null pointer is not welcome.
     */
    private Context context = Context.create();

    public UnitResponse setContext(Context context) {
        this.context = context;
        return this;
    }

    /**
     * @return the exception object if this unit response represents a exception response. Null if no exception.
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Set the exception of this unit response, note that this will set the rollback property in the context to true,
     * which will lead to a transaction rolling back if transaction is enabled.
     *
     * @param exception the exception object.
     * @return this unit response object.
     */
    public UnitResponse setException(Throwable exception) {
        this.exception = exception;
        getContext().setRollback(true);
        return this;
    }

    private UnitResponse() {
    }

    public Context getContext() {
        return context;
    }

    /**
     * create a succeeded response instance.
     */
    public static UnitResponse createSuccess() {
        return new UnitResponse().setCode(Group.CODE_SUCCESS);
    }

    /**
     * create a succeeded response instance with specified data.
     */
    public static UnitResponse createSuccess(Object data) {
        return UnitResponse.createSuccess().setData(data);
    }

    /**
     * Please pass an exception object to this method, and it returns a newly created response object with error code {@link Group#CODE_EXCEPTION}
     * and the exception object as the data.
     *
     * @param e the exception object.
     */
    public static UnitResponse createException(Throwable e) {
        return UnitResponse
                .createError(Group.CODE_EXCEPTION, null, null)
                .setException(e);
    }

    /**
     * Please pass an exception object to this method, and it returns a newly created response object with error code {@link Group#CODE_EXCEPTION}
     * and the exception object as the data.
     *
     * @param e      the exception object.
     * @param errMsg the error message that you want to add.
     */
    public static UnitResponse createException(Throwable e, String errMsg) {
        return UnitResponse.createException(e).setErrMsg(errMsg);
    }

    /**
     * Create an exception unit response object.
     *
     * @param errCode   the error code for the unit response.
     * @param exception the exception object.
     * @return the newly created unit response.
     */
    public static UnitResponse createException(String errCode, Throwable exception) {
        return UnitResponse.createException(exception).setCode(errCode);
    }

    public static UnitResponse createException(String errCode, Throwable exception, String errMsg) {
        return UnitResponse.createException(errCode, exception).setErrMsg(errMsg);
    }

    /**
     * @param data   The failure data. Leave it null if you have no data to set.
     *               Under some complicated unit situations, failed data must be included and returned.
     * @param errMsg failure reason or description.
     * @return an response object with failure data and failure message.
     */
    public static UnitResponse createUnknownError(Object data, String errMsg) {
        return createError(Group.CODE_UNKNOWN_ERROR, data, errMsg);
    }

    /**
     * create a new error unit response instance.
     *
     * @param errCode the error code.
     * @param data    the data
     * @param errMsg  the error message.
     * @return the newly created unit response object.
     */
    public static UnitResponse createError(String errCode, Object data, String errMsg) {
        if (Group.CODE_SUCCESS.equalsIgnoreCase(errCode)) {
            throw new IllegalArgumentException("Only non-success code is allowed here.");
        }
        return new UnitResponse().setCode(errCode).setData(data).setErrMsg(errMsg);
    }

    /**
     * @param data   the value
     * @param errMsg error message.
     * @return the newly created response instance.
     */
    public static UnitResponse createDataDoesNotExists(Object data, String errMsg) {
        return createError(Group.CODE_DATA_DOES_NOT_EXITS, data, errMsg);
    }

    /**
     * @param code   {@link Group#CODE_SUCCESS SUCCESS}, {@link Group#CODE_UNKNOWN_ERROR FAILURE} etc.
     * @param data   the data bean or json
     * @param errMsg the error message. Note that errMsg must be null when the {{@link #code}} is {@link Group#CODE_SUCCESS SUCCESS}
     */
    public static UnitResponse create(String code, Object data, String errMsg) {
        return new UnitResponse().setCode(code).setData(data).setErrMsg(errMsg);
    }

    /**
     * Create neither an success response object or a failure one.
     *
     * @param successful true to return an succeeded response object, false the opposite.
     */
    public static UnitResponse create(boolean successful) {
        if (successful) {
            return createSuccess();
        } else {
            return createUnknownError(null, null);
        }
    }

    /**
     * create a rollback marked unit response
     *
     * @return the newly created unit reponse instance.
     */
    public static UnitResponse createRollingBack() {
        return createRollingBack(null);
    }

    /**
     * Create a rolling back response object with the given errMsg.
     *
     * @param errMsg the error message.
     * @return An response object which will indicate a transactional rolling back.
     */
    public static UnitResponse createRollingBack(String errMsg) {
        return UnitResponse.createUnknownError(null, errMsg).setContext(Context.create().setRollback(true));
    }

    /**
     * create a new unit response when missingParam
     *
     * @param theMissingParameters theMissingParameters array, leave it null, if you dont know which parameter is missing.
     * @return the newly created response object describing the missed parameters.
     */
    public static UnitResponse createMissingParam(Object theMissingParameters, String errMsg) {
        return UnitResponse.createError(Group.CODE_LACK_OF_PARAMETER, theMissingParameters, errMsg);
    }

    @SuppressWarnings("unchecked")
    public static UnitResponse create(JSONObject json) {
        return Reflection.toType(json, UnitResponse.class);
    }

    public UnitResponse setCode(String code) {
        this.code = code;
        return this;
    }

    public String getCode() {
        return code;
    }

    public UnitResponse setData(Object data) {
        this.data = data;
        return this;
    }

    /**
     * Get the typed object directly.
     *
     * @return The casted object.
     */
    public Object getData() {
        return data;
    }

    public String dataToStr() {
        return data == null ? null : data.toString();
    }

    public Integer dataToInteger() {
        String dataStr = dataToStr();
        return dataStr == null ? null : Integer.valueOf(dataStr);
    }

    public Long dataToLong() {
        String dataStr = dataToStr();
        return dataStr == null ? null : Long.valueOf(dataStr);
    }

    public Boolean dataToBoolean() {
        return data == null ? null : Boolean.valueOf(dataToStr());
    }

    /**
     * @return a bool value true/false
     * @throws NullPointerException if data is null
     */
    public boolean dataToBoolValue() {
        return Reflection.toType(data, boolean.class);
    }

    public Double dataToDouble() {
        return Reflection.toType(data, Double.class);
    }

    public BigDecimal dataToBigDecimal() {
        return Reflection.toType(data, BigDecimal.class);
    }

    public JSONObject dataToJson() {
        return dataToType(JSONObject.class);
    }

    public JSONObject firstToJson() {
        return dataToJson();
    }

    public JSONArray dataToJsonArray() {
        return dataToType(JSONArray.class);
    }

    /**
     * data to Page
     */
    public Page dataToPage() {
        if (data instanceof String) {
            return Page.create(data.toString());
        }
        if (data instanceof JSONObject) {
            return Page.create((JSONObject) data);
        }
        if (data instanceof Page) {
            return (Page) data;
        }
        throw new RuntimeException(toString() + "  不是page!");
    }

    /**
     * 将unitResponse.data适配为map的列表，请放心大胆做转换
     */
    public List<JSONObject> dataToListOfJsonObject() {
        return dataToTypedList(JSONObject.class);
    }

    /**
     * 将unitResponse.data适配为指定的类型，请放心大胆做转换
     */
    public <T> T dataToType(Class<T> type) {
        return Reflection.toType(data, type);
    }

    /**
     * 将unitResponse.data适配为指定类型的list，请放心大胆做转换
     */
    public <T> List<T> dataToTypedList(Class<T> type) {
        return Reflection.toTypedList(data, type);
    }

    /**
     * The same as {@link #toVoJSONString()} ()}
     */
    public String toString() {
        return toVoJSONString();
    }

    /**
     * Standard json formation without data lost.
     */
    public String toJSONString() {
        if (context.isPretty())
            return JSON.toJSONStringWithDateFormat(this, Constant.DATE_SERIALIZE_FORMAT, SerializerFeature.PrettyFormat);
        else
            return JSONObject.toJSONStringWithDateFormat(this, Constant.DATE_SERIALIZE_FORMAT);
    }

    /**
     * Our api gateway uses this method instead of {@link #toJSONString()} {@link #toString()} to produce a desensitized output VO json string.
     * For detail, see the return description.
     *
     * @return json string with sensitive properties(the context property) hidden.
     */
    public String toVoJSONString() {
        if (context.isPretty())
            return JSON.toJSONStringWithDateFormat(toVoJSONObject(), Constant.DATE_SERIALIZE_FORMAT, SerializerFeature.PrettyFormat);
        else
            return JSONObject.toJSONStringWithDateFormat(toVoJSONObject(), Constant.DATE_SERIALIZE_FORMAT);
    }

    /**
     * No matter context pretty attribute is true or not, this method formats the json string you want.
     *
     * @param pretty pretty or not.
     * @return formatted json string.
     */
    public String toVoJSONString(boolean pretty) {
        if (pretty)
            return JSON.toJSONStringWithDateFormat(toVoJSONObject(), Constant.DATE_SERIALIZE_FORMAT, SerializerFeature.PrettyFormat);
        else
            return JSONObject.toJSONStringWithDateFormat(toVoJSONObject(), Constant.DATE_SERIALIZE_FORMAT);
    }

    /**
     * Our api gateway uses this method to generate a desensitized json object instead of the {@link #toJSONObject()} method.
     *
     * @return JSONObject with the sensitive properties(context property) hidden.
     */
    public JSONObject toVoJSONObject() {
        return toJSONObject().fluentPut("context", null);
    }

    /**
     * convert this response object into json object.
     */
    public JSONObject toJSONObject() {
        try {
            return Reflection.toType(this, JSONObject.class);
        } catch (Throwable e) {
            throw new RuntimeException("The output cannot be converted to jsonObject!", e);
        }
    }

    public UnitResponse throwExceptionIfNotSuccess() {
        if (!succeeded()) {
            throw new RuntimeException(toString());
        }
        return this;
    }

    public UnitResponse throwExceptionIfNotSuccess(String exceptionMsg) {
        if (!succeeded()) {
            throw new RuntimeException(exceptionMsg, new Throwable(toString()));
        }
        return this;
    }

    /**
     * Judge weather the code is 'SUCCESS'.
     */
    public boolean succeeded() {
        return Group.CODE_SUCCESS.equals(code);
    }

    /**
     * return message id of this response object.
     * This getter method is prepared for json serialization.
     */
    public String getMsgId() {
        return context.getMsgId();
    }

    /**
     * @param successCall 若当前output的code为SUCCESS时，该方法将会被执行(建议使用lambda表达式).
     */
    public UnitResponse successCall(Function<UnitResponse, UnitResponse> successCall) {
        if (successCall == null || !succeeded()) {
            return this;
        }
        try {
            return successCall.apply(this);
        } catch (Exception e) {
            LOG.error(e);
            return UnitResponse.createException(e);
        }
    }

    /**
     * @param successCall 若当前output的code为SUCCESS时，该方法将会被执行(建议使用lambda表达式).
     */
    public UnitResponse successCall(Callable<UnitResponse> successCall) {
        if (successCall == null || !succeeded()) {
            return this;
        }
        try {
            return successCall.call();
        } catch (Throwable e) {
            LOG.error(e);
            return UnitResponse.createException(e);
        }
    }

    /**
     * @param successCall 若当前output的code为SUCCESS时，该方法将会被执行(建议使用lambda表达式).
     */
    public void successCall(Consumer<UnitResponse> successCall) {
        if (successCall != null && succeeded()) {
            try {
                successCall.accept(this);
            } catch (Throwable e) {
                LOG.error(e);
            }
        }
    }

    public String getErrMsg() {
        if (succeeded() && errMsg != null) {
            LOG.warn("成功的output禁止使用errMsg属性：errMsg= " + errMsg);
            return null;
        }
        return errMsg;
    }

    public UnitResponse setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    /**
     * 将data转为{@link InputStream}
     */
    public InputStream dataToStream() {
        return dataToType(InputStream.class);
    }

    /**
     * 一行一行地处理inputStream，串行处理。
     *
     * @param function 处理函数，入参为流的每一行字符串
     */
    public void processStreamLineByLine(Function<String, Object> function) {
        StreamUtil.lineByLine(dataToStream(), function);
    }

    /**
     * Process the data InputStream Part By Part orderly.
     * Only use this method when data of this response object is an input stream.
     *
     * @param function         the processing function whose parameter is the input stream part.
     * @param delimiterPattern 分段分隔符，支持正则表达式
     */
    public void processStreamPartByPart(String delimiterPattern, Function<String, Object> function) {
        StreamUtil.partByPart(dataToType(InputStream.class), delimiterPattern, function);
    }

    /**
     * copy all properties from one response object ot another response object.
     *
     * @param from the source
     */
    public static UnitResponse clone(UnitResponse from) {
        return from.clone();
    }

    /**
     * copy all properties from one response object ot another response object.
     *
     * @param from the source
     * @param to   the destiny
     */
    public static void copy(UnitResponse from, UnitResponse to) {
        //remember to modify this method when new properties are added.
        to.setCode(from.code).setData(from.data).setContext(from.context.clone()).setErrMsg(from.errMsg);
    }

    @Override
    protected UnitResponse clone() {
        return CloneUtil.cloneBean(this, UnitResponse.class);
    }

    /**
     * read the value of the specified index and key in this response object.
     *
     * @param i   the index
     * @param key the key
     * @return the value
     */
    public Object value(int i, String key) {
        Object data = getData();
        if (data instanceof List) {
            List list = (List) data;
            if (list.size() > 0) {
                Map map = (Map) list.get(i);
                return map.get(key);
            }
        } else if (data instanceof Map) {
            return ((Map) data).get(key);
        }
        return null;
    }

    public static final class Context {
        /**
         * whether or not to format a pretty output json.
         */
        private boolean pretty = false;
        /**
         * node id which this response object is from.
         * Defaults to local node id, because response object is always created from the local node, and then be transported
         * to remote.
         */
        private String sourceNodeId = LocalNodeManager.LOCAL_NODE_ID;
        /**
         * where this response object is aimed to be transported to.
         */
        private String destinationNodeId;
        private boolean rollback = false;
        /**
         * Use message id if you know what you are doing.
         * Defaults to context message id.
         */
        private String msgId = MsgIdHolder.get();

        private String ssid;

        private NodeStatus nodeStatus;

        private long creationTimestamp;//timestamp for response creation
        private long sentTimestamp;//message sent time in milli

        /**
         * defaults to {@link MessageType#response},
         * and can be {@link MessageType#responseStream} too.
         */
        private MessageType messageType = MessageType.response;

        /**
         * the {@link HttpContentType content type} for the gateway to response in the http header.
         */
        private String httpContentType;

        public String getHttpContentType() {
            return httpContentType;
        }

        /**
         * @param httpContentType the {@link HttpContentType content type string}
         * @return this context object.
         * @see HttpContentType
         */
        public Context setHttpContentType(String httpContentType) {
            this.httpContentType = httpContentType;
            return this;
        }

        public String getSsid() {
            return ssid;
        }

        public Context setSsid(String ssid) {
            this.ssid = ssid;
            return this;
        }

        public String getMsgId() {
            return msgId;
        }

        public Context setMsgId(String msgId) {
            this.msgId = msgId;
            return this;
        }

        public static Context create() {
            return new Context();
        }

        public boolean isPretty() {
            return pretty;
        }

        public Context setPretty(boolean pretty) {
            this.pretty = pretty;
            return this;
        }

        public String getSourceNodeId() {
            return sourceNodeId;
        }

        public Context setSourceNodeId(String sourceNodeId) {
            this.sourceNodeId = sourceNodeId;
            return this;
        }

        public String getDestinationNodeId() {
            return destinationNodeId;
        }

        public Context setDestinationNodeId(String destinationNodeId) {
            this.destinationNodeId = destinationNodeId;
            return this;
        }

        public boolean isRollback() {
            return rollback;
        }

        public Context setRollback(boolean rollback) {
            this.rollback = rollback;
            return this;
        }

        public NodeStatus getNodeStatus() {
            return nodeStatus;
        }

        public Context setNodeStatus(NodeStatus nodeStatus) {
            this.nodeStatus = nodeStatus;
            return this;
        }

        public MessageType getMessageType() {
            return messageType;
        }

        public Context setMessageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public long getCreationTimestamp() {
            return creationTimestamp;
        }

        public Context setCreationTimestamp(long creationTimestamp) {
            this.creationTimestamp = creationTimestamp;
            return this;
        }

        public long getSentTimestamp() {
            return sentTimestamp;
        }

        public Context setSentTimestamp(long sentTimestamp) {
            this.sentTimestamp = sentTimestamp;
            return this;
        }

        @Override
        protected Context clone() {
            return CloneUtil.cloneBean(this, Context.class);
        }
    }

}
