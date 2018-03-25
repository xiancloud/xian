package info.xiancloud.qcloudcos.api.http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.qcloud.cos.http.RequestBodyKey;

public class HttpRequest {

	private String url;
	private HttpMethod method = HttpMethod.POST;
	private HttpContentType contentType = HttpContentType.MULTIPART_FORM_DATA;
	private Map<String, String> headers = new LinkedHashMap<>();
	private Map<String, String> params = new LinkedHashMap<>();
	private byte[] bytes;

	public HttpRequest() {
		super();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public HttpContentType getContentType() {
		return contentType;
	}

	public void setContentType(HttpContentType contentType) {
		this.contentType = contentType;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	public void addParam(String key, String value) {
		this.params.put(key, value);
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("url:").append(url).append(", method:").append(method).append(", ConentType:")
				.append(contentType.toString()).append("\n");

		sb.append("Headers:\n");
		for (Entry<String, String> entry : headers.entrySet()) {
			sb.append("key:").append(entry.getKey());
			sb.append(", value:").append(entry.getValue());
			sb.append("\n");
		}

		sb.append("params:\n");
		for (Entry<String, String> entry : params.entrySet()) {
			if (entry.getKey().equals(RequestBodyKey.FILE_CONTENT)) {
				continue;
			}
			sb.append("key:").append(entry.getKey());
			sb.append(", value:").append(entry.getValue());
			sb.append("\n");
		}

		return sb.toString();
	}
}
