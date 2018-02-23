package info.xiancloud.wxcp.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class WxAccessToken implements Serializable {
	private static final long serialVersionUID = 8709719312922168909L;

	private String accessToken;

	private int expiresIn = -1;

	public static WxAccessToken fromJson(String json) {
		return JSON.parseObject(json, WxAccessToken.class);
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getExpiresIn() {
		return this.expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

}
