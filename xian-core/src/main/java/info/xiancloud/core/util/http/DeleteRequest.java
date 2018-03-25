package info.xiancloud.core.util.http;

public class DeleteRequest extends Request {

	private static final long serialVersionUID = 8151544316296870854L;

	public DeleteRequest(String url) {
		super(url);
		method = HttpMethod.DELETE.name();
	}

}
