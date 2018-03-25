package com.apifest.oauth20.unit;

import com.apifest.oauth20.Authenticator;
import info.xiancloud.core.Group;

/**
 * oauth2.0 unit group
 *
 * @author happyyangyuan
 */
public class OAuthService implements Group {
    static final Authenticator auth = new Authenticator();
    public static final Group singleton = new OAuthService();

    @Override
    public String getName() {
        return "OAuth";
    }

    @Override
    public String getDescription() {
        return "授权相关API";
    }
}
