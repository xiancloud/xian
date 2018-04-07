package info.xiancloud.core;

import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit meta info.
 *
 * @author happyyangyuan
 */
public final class UnitMeta {

    private String description;
    /**
     * todo rename to apidoc
     */
    private boolean publik = true;//public是关键字，所以这里取名为publik，但是getter和setter命名还是isPublic()和setPublic()
    /**
     * whether or not to use transaction.
     */
    private boolean transactional = false;
    /**
     * Indicate the persistence unit whether or not to read data from slave database.
     * Defaults to false.
     */
    private boolean readonly = false;
    /**
     * Indicate that the unit is broadcasting or not.
     * Defaults to null, which means no broadcasting.
     */
    private Broadcast broadcast = null;
    /**
     * unit权限scope，所有的unit默认都在ScopeEnum.api_all内
     */
    private Set<String> scopes = new HashSet<String>() {{
        add(Scope.api_all);
    }};
    /**
     * 是否采集监控数据，目前支持的监控数据有: unit被调用瞬时并发数
     */
    private boolean monitorEnabled = false;
    /**
     * unit是否可离线中转,默认false
     */
    private boolean transferable = false;

    private UnitResponse successfulUnitResponse = UnitResponse.createSuccess();

    private List<UnitResponse> failedUnitResponses = new ArrayList<UnitResponse>() {{
        add(UnitResponse.createUnknownError(null, "error message."));
        add(UnitResponse.createMissingParam(new ArrayList<String>() {{
            add("the missing param 0");
            add("the missing param 1");
            add("the missing param 2");
            add("the missing param ...");
            add("the missing param n");
        }}, "Missing parameters"));
    }};

    /**
     * defines whether or not to check access token in api gateway, if secure is true then check else not.
     * defaults to true
     */
    private boolean secure = true;

    private boolean bodyRequired = false;

    private boolean dataOnly = false;

    private String version = "1.0";

    /**
     * create a new unit meta object.
     */
    public static UnitMeta create() {
        return new UnitMeta();
    }

    /**
     * create a new unit meta object with specified description.
     *
     * @param description the description.
     */
    public static UnitMeta createWithDescription(String description) {
        return create().setDescription(description);
    }

    private UnitMeta() {
    }

    public String getDescription() {
        return description;
    }

    public UnitMeta setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isPublic() {
        return publik;
    }

    /**
     * Set this unit to be public or private, currently only public units are shown in api doc.
     *
     * @param isPublic true: is public，false：private<br>
     *                 defaults to true
     */
    public UnitMeta setPublic(boolean isPublic) {
        this.publik = isPublic;
        return this;
    }

    /**
     * @deprecated not supported yet
     */
    public boolean isTransactional() {
        return transactional;
    }

    /**
     * @deprecated not supported yet
     */
    public UnitMeta setTransactional(boolean transactional) {
        this.transactional = transactional;
        return this;
    }

    public Broadcast getBroadcast() {
        return broadcast;
    }

    /**
     * set the broadcast mode.
     */
    public UnitMeta setBroadcast(Broadcast broadcast) {
        this.broadcast = broadcast;
        return this;
    }

    /**
     * set the default broadcast mode.
     */
    public UnitMeta setBroadcast() {
        setBroadcast(new Broadcast());
        return this;
    }

    /**
     * set this unit is readonly for database.
     */
    public UnitMeta setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public UnitMeta addScope(String scopeEnum) {
        scopes.add(scopeEnum);
        return this;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public UnitMeta setScopes(Set<String> scopes) {
        if (scopes == null) {
            this.scopes = new HashSet<>();
        } else {
            this.scopes = scopes;
        }
        this.scopes.add(Scope.api_all);
        return this;
    }

    public boolean isMonitorEnabled() {
        return monitorEnabled;
    }

    public UnitMeta setMonitorEnabled(boolean monitorEnabled) {
        this.monitorEnabled = monitorEnabled;
        return this;
    }

    public boolean isTransferable() {
        return transferable;
    }

    public UnitMeta setTransferable(boolean transferable) {
        this.transferable = transferable;
        return this;
    }

    /**
     * unit定义的广播属性
     */
    public static final class Broadcast {
        private boolean successDataOnly = false;
        /**
         * if true the broadcast sender returns immediately, otherwise blocks until all unit returned,
         * note that the receiver unit performs concurrently rather than one by one.
         *
         * @deprecated In synchronous xian, this async property is not needed anymore.
         */
        private boolean async = true;
        private long timeoutInMilli = 5000;

        public boolean isSuccessDataOnly() {
            return successDataOnly;
        }

        public Broadcast setSuccessDataOnly(boolean successDataOnly) {
            this.successDataOnly = successDataOnly;
            return this;
        }

        /**
         * @deprecated In synchronous xian, this async property is not needed anymore.
         */
        public boolean isAsync() {
            return async;
        }

        /**
         * @deprecated In synchronous xian, this async property is not needed anymore.
         */
        public Broadcast setAsync(boolean async) {
            this.async = async;
            return this;
        }

        public long getTimeoutInMilli() {
            return timeoutInMilli;
        }

        public Broadcast setTimeoutInMilli(long timeoutInMilli) {
            this.timeoutInMilli = timeoutInMilli;
            return this;
        }

        public static Broadcast create() {
            return new Broadcast();
        }
    }

    public boolean isSecure() {
        return secure;
    }

    public UnitMeta setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public boolean isBodyRequired() {
        return bodyRequired;
    }

    /**
     * Indicate whether to keep the original request body in http request.
     * If set to true, you can get the whole request body by calling {@link UnitRequest#getContext()} #getBody()} method.
     * If set to false, {@link UnitRequest#getContext()} #getBody()} method returns false.
     * Defaults to false if you do not speicify the value.
     *
     * @param bodyRequired true to keep the body string in the {@link UnitRequest}, false otherwise.
     */
    public UnitMeta setBodyRequired(boolean bodyRequired) {
        this.bodyRequired = bodyRequired;
        return this;
    }

    public boolean isDataOnly() {
        return dataOnly;
    }

    /**
     * Indicate that the api gateway only to return the data object in your response object to the client.
     * If you do not specify this value, it defaults to false.
     *
     * @param dataOnly true means data only, false otherwise.
     */
    public UnitMeta setDataOnly(boolean dataOnly) {
        this.dataOnly = dataOnly;
        return this;
    }

    public UnitResponse getSuccessfulUnitResponse() {
        return successfulUnitResponse;
    }

    /**
     * Tell the api doc plugin to generate the successful response for this unit.
     * Defaults to {@link #successfulUnitResponse}
     *
     * @param successfulUnitResponse the successful output  demo object.
     */
    public UnitMeta setSuccessfulUnitResponse(UnitResponse successfulUnitResponse) {
        this.successfulUnitResponse = successfulUnitResponse;
        return this;
    }

    public List<UnitResponse> getFailedUnitResponses() {
        return failedUnitResponses;
    }

    /**
     * Tell the api doc plugin to generate the failed reponse for this unit.
     * Defaults to {@link #failedUnitResponses}
     *
     * @param failedUnitResponses the failed output demo object list.
     */
    public void setFailedUnitResponses(List<UnitResponse> failedUnitResponses) {
        this.failedUnitResponses = failedUnitResponses;
    }

    public String getVersion() {
        return version;
    }

    /**
     * this version is without any meaning currently, please ignore it.
     */
    public UnitMeta setVersion(String version) {
        this.version = version;
        return this;
    }
}
