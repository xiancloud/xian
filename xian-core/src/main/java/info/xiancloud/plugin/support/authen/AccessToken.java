package info.xiancloud.plugin.support.authen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import info.xiancloud.plugin.util.LOG;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Represents an access token.
 *
 * @author Rossitsa Borissova
 */
public class AccessToken implements Serializable {

    @JSONField(name = "access_token", ordinal = 1)
    private String token = "";

    // not included when client_credentials
    @JSONField(name = "refresh_token", ordinal = 2)
    private String refreshToken = "";

    // bearer or mac
    @JSONField(name = "token_type", ordinal = 3)
    private String type = "";

    @JSONField(name = "expires_in", ordinal = 4)
    private String expiresIn = "";

    private String scope = "";

    @JSONField
    private boolean valid;

    @JSONField(name = "client_id")
    private String clientId = "";

    @JSONField(serialize = false, deserialize = false)
    private String codeId = "";

    @JSONField(name = "user_id")
    private String userId = "";

    @JSONField(serialize = false, deserialize = false)
    private Map<String, String> applicationDetails = null;

    @JSONField(serialize = false, deserialize = false)
    private Map<String, String> details = null;

    @JSONField
    private Long created;

    @JSONField(name = "refresh_expires_in", ordinal = 7)
    private String refreshExpiresIn = "";

    /**
     * Creates access token along with its refresh token.
     *
     * @param tokenType
     * @param expiresIn
     * @param scope
     */
    public AccessToken(String tokenType, String expiresIn, String scope, String refreshExpiresIn) {
        this(tokenType, expiresIn, scope, true, refreshExpiresIn);
    }

    /**
     * Creates access token. Used for generation of client_credentials type tokens with no refreshToken.
     *
     * @param tokenType
     * @param expiresIn
     * @param scope
     * @param createRefreshToken
     */
    public AccessToken(String tokenType, String expiresIn, String scope, boolean createRefreshToken, String refreshExpiresIn) {
        this.token = RandomGenerator.generateRandomString();
        if (createRefreshToken) {
            this.refreshToken = RandomGenerator.generateRandomString();
            this.refreshExpiresIn = (refreshExpiresIn != null && !refreshExpiresIn.isEmpty()) ? refreshExpiresIn : expiresIn;
        }
        this.expiresIn = expiresIn;
        this.type = tokenType;
        this.scope = scope;
        this.valid = true;
        this.created = (new Date()).getTime();
    }

    /**
     * Creates access token with already generated refresh token.
     *
     * @param tokenType
     * @param expiresIn
     * @param scope
     * @param refreshToken
     */
    public AccessToken(String tokenType, String expiresIn, String scope, String refreshToken, String refreshExpiresIn) {
        this.token = RandomGenerator.generateRandomString();
        this.expiresIn = expiresIn;
        this.type = tokenType;
        this.scope = scope;
        this.valid = true;
        this.created = (new Date()).getTime();
        this.refreshToken = refreshToken;
        this.refreshExpiresIn = (refreshExpiresIn != null && !refreshExpiresIn.isEmpty()) ? refreshExpiresIn : expiresIn;
    }

    public AccessToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String accessToken) {
        this.token = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getType() {
        return type;
    }

    public void setType(String tokenType) {
        this.type = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isValid() {
        return valid;
    }

    public AccessToken setValid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public Map<String, String> getApplicationDetails() {
        return applicationDetails;
    }

    public void setApplicationDetails(Map<String, String> applicationDetails) {
        this.applicationDetails = applicationDetails;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(String refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public static AccessToken loadFromMap(Map<String, Object> map) {
        AccessToken accessToken = new AccessToken();
        accessToken.token = (String) map.get("token");
        accessToken.refreshToken = (String) map.get("refreshToken");
        accessToken.expiresIn = (String) map.get("expiresIn");
        accessToken.type = (String) map.get("type");
        accessToken.scope = (String) map.get("scope");
        accessToken.valid = (Boolean) map.get("valid");
        accessToken.clientId = (String) map.get("LOCAL_NODE_ID");
        accessToken.codeId = (String) map.get("codeId");
        accessToken.userId = (String) map.get("userId");
        accessToken.created = (Long) map.get("created");
        accessToken.details = convertStringToMap((String) map.get("details"));
        accessToken.applicationDetails = convertStringToMap((String) map.get("applicationDetails"));
        accessToken.refreshExpiresIn = (String) ((map.get("refreshExpiresIn") != null ? map.get("refreshExpiresIn") : accessToken.expiresIn));
        return accessToken;
    }

    public static AccessToken loadFromStringMap(Map<String, String> map) {
        AccessToken accessToken = new AccessToken();
        accessToken.token = map.get("token");
        accessToken.refreshToken = map.get("refreshToken");
        accessToken.expiresIn = map.get("expiresIn");
        accessToken.type = map.get("type");
        accessToken.scope = map.get("scope");
        accessToken.valid = Boolean.parseBoolean(map.get("valid"));
        accessToken.clientId = map.get("LOCAL_NODE_ID");
        accessToken.codeId = map.get("codeId");
        accessToken.userId = map.get("userId");
        accessToken.created = Long.parseLong(map.get("created"));
        accessToken.details = convertStringToMap(map.get("details"));
        accessToken.applicationDetails = convertStringToMap(map.get("applicationDetails"));
        accessToken.refreshExpiresIn = map.get("refreshExpiresIn") != null ? map.get("refreshExpiresIn") : accessToken.expiresIn;
        return accessToken;
    }

    public static AccessToken loadFromStringList(List<String> list) {
        AccessToken accessToken = new AccessToken();
        accessToken.token = list.get(0);
        accessToken.refreshToken = list.get(1);
        ;
        accessToken.expiresIn = list.get(2);
        accessToken.type = list.get(3);
        accessToken.scope = list.get(4);
        accessToken.valid = Boolean.parseBoolean(list.get(5));
        accessToken.clientId = list.get(6);
        accessToken.codeId = list.get(7);
        accessToken.userId = list.get(8);
        accessToken.created = Long.parseLong(list.get(9));
        accessToken.details = convertStringToMap(list.get(10));
        accessToken.refreshExpiresIn = list.get(11) != null ? list.get(11) : accessToken.expiresIn;
        accessToken.applicationDetails = convertStringToMap(list.get(12));
        return accessToken;
    }

    public boolean tokenExpired() {
        // expires_in is in seconds
        Long expiresInMillis = Long.valueOf(getExpiresIn()) * 1000;
        Long currentTime = System.currentTimeMillis();
        if (expiresInMillis + getCreated() < currentTime) {
            return true;
        }
        return false;
    }

    public boolean refreshTokenExpired() {
        Long refreshExpiresInSec = Long.valueOf(getRefreshExpiresIn()) * 1000;
        Long currentTime = System.currentTimeMillis();
        if (refreshExpiresInSec + getCreated() < currentTime) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        try {
            return JSON.toJSONString(this);
        } catch (Throwable e) {
            LOG.error(e);
            return super.toString();
        }
    }

    private static String convertMapToJSON(Map<String, String> list) {
        return JSON.toJSONString(list);
    }

    private static Map<String, String> convertStringToMap(String json) {
        return (Map) JSON.parseObject(json);
    }
}
