package info.xiancloud.apifestoauth20.unit;

import com.apifest.oauth20.Authenticator;
import com.apifest.oauth20.ScopeService;
import info.xiancloud.core.Group;

/**
 * oauth2.0 unit group
 *
 * @author happyyangyuan
 */
public class OAuthService implements Group {
    /**
     * token error or token expired
     */
    public static final String CODE_BAD_TOKEN = "BAD_TOKEN";
    public static final Authenticator auth = new Authenticator();
    public static final Group singleton = new OAuthService();

    public static ScopeService getScopeService() {
        return new ScopeService();
    }

    @Override
    public String getName() {
        return "OAuth";
    }

    @Override
    public String getDescription() {
        return "授权相关API";
    }
}
