package info.xiancloud.qcloudcos.api.http;

/**
 * 请求类型
 * 
 * @author yyq
 *
 */
public enum HttpContentType {
	
	TEXT_PLAIN("text/plain"),

	APPLICATION_JSON("application/json"),

	MULTIPART_FORM_DATA("multipart/form-data"),
	
    OCTET_STREAM("application/octet-stream");

	private String contentType;

	private HttpContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public String toString() {
		return this.contentType;
	}
}
