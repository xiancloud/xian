package info.xiancloud.core.util.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

class RequestBody implements Serializable {
	
	
	private static final long serialVersionUID = -1757382347463770568L;
	
	private HashMap<String, Object> params;
	// content里面设置了内容的话，发送请求时，params里面的值将不会再发送
	private String content;

	public RequestBody() {

	}

	public RequestBody addParam(String pName, Object pValue) {
		if (params == null)
			params = new HashMap<String, Object>();
		params.put(pName, pValue);
		return this;
	}

	public RequestBody setContent(String content) {
		this.content = content;
		return this;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public String getContent() {
		return content;
	}
}
