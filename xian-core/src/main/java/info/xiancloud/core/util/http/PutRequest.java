package info.xiancloud.core.util.http;

public class PutRequest extends Request {

	
	private static final long serialVersionUID = -8876220156482796695L;

	public PutRequest(String url) {
		super(url);
		method = HttpMethod.PUT.name();
	}

}
