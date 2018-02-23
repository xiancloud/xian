package info.xiancloud.plugin.util.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Header implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6351087257596720351L;
	private HashMap<String, String> headers;

	public Header() {

	}

	public Header addHeader(String name, String value) {
		if (headers == null)
			headers = new HashMap<String, String>();
		this.headers.put(name, value);
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}
