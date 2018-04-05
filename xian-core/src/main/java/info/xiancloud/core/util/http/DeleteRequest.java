package info.xiancloud.core.util.http;

public class DeleteRequest extends Request {

	public DeleteRequest(String url) {
		super(url);
		method = HttpMethod.DELETE.name();
	}

}
