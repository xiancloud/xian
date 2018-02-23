package info.xiancloud.wxcp.api;


import java.io.File;

import info.xiancloud.wxcp.bean.WxAccessToken;

/**
 * 微信客户端配置存储
 *
 */
public interface WxCpConfigStorage {

  String getAccessToken();

  boolean isAccessTokenExpired();

  /**
   * 强制将access token过期掉
   */
  void expireAccessToken();

  void updateAccessToken(WxAccessToken accessToken);

  void updateAccessToken(String accessToken, int expiresIn);

  String getCorpId();

  String getCorpSecret();

  Integer getAgentId();

  String getToken();

  String getAesKey();

  long getExpiresTime();

  String getOauth2redirectUri();


  File getTmpDirFile();

}
