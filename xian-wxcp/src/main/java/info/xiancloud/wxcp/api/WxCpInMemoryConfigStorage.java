package info.xiancloud.wxcp.api;

import java.io.File;

import info.xiancloud.wxcp.bean.WxAccessToken;

/**
 * 基于内存的微信配置provider，在实际生产环境中应该将这些配置持久化
 *
 */
public class WxCpInMemoryConfigStorage implements WxCpConfigStorage {

	protected volatile String corpId;// 企业ID
	protected volatile String corpSecret;// 企业密匙

	protected volatile String token;
	protected volatile String accessToken;
	protected volatile String aesKey;
	protected volatile Integer agentId;
	protected volatile long expiresTime;

	protected volatile String oauth2redirectUri;
	protected volatile File tmpDirFile;

	/**
	 * FIXME 先硬编码了，以后再扩展 BY yyq
	 */
	public WxCpInMemoryConfigStorage() {
		this.corpId = "wx2af25f22aaddc207";
		this.corpSecret = "5u3p0eKZHGnyqLApw3q-Y-wyJNg-WOJbFVyI1BaGwn8";
		this.agentId = 0;
	}

	@Override
	public String getAccessToken() {
		return this.accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public boolean isAccessTokenExpired() {
		return System.currentTimeMillis() > this.expiresTime;
	}

	@Override
	public void expireAccessToken() {
		this.expiresTime = 0;
	}

	@Override
	public synchronized void updateAccessToken(WxAccessToken accessToken) {
		updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
	}

	@Override
	public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
		this.accessToken = accessToken;
		this.expiresTime = System.currentTimeMillis() + (expiresInSeconds - 200) * 1000l;
	}

	@Override
	public String getCorpId() {
		return this.corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	@Override
	public String getCorpSecret() {
		return this.corpSecret;
	}

	public void setCorpSecret(String corpSecret) {
		this.corpSecret = corpSecret;
	}

	@Override
	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public long getExpiresTime() {
		return this.expiresTime;
	}

	public void setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
	}

	@Override
	public String getAesKey() {
		return this.aesKey;
	}

	public void setAesKey(String aesKey) {
		this.aesKey = aesKey;
	}

	@Override
	public Integer getAgentId() {
		return this.agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	@Override
	public String getOauth2redirectUri() {
		return this.oauth2redirectUri;
	}

	public void setOauth2redirectUri(String oauth2redirectUri) {
		this.oauth2redirectUri = oauth2redirectUri;
	}

	@Override
	public File getTmpDirFile() {
		return this.tmpDirFile;
	}

	public void setTmpDirFile(File tmpDirFile) {
		this.tmpDirFile = tmpDirFile;
	}

}
