package info.xiancloud.plugin.util.http;

import java.io.IOException;
import java.net.URLEncoder;

public class PostRequest extends Request {

	private static final long serialVersionUID = -2770100854598679748L;

	public PostRequest(String url) {
		super(url);
		method = HttpMethod.POST.name();
	}

	@Override
	protected void generateBody() throws IOException {
		if (body != null && body.getContent() == null) {
			StringBuilder params = new StringBuilder();
			for (String key : this.body.getParams().keySet()) {
				String value = this.body.getParams().get(key).toString();
				params.append(key).append("=").append(URLEncoder.encode(value, charset));
				params.append("&");
			}
			params.deleteCharAt(params.length() - 1);
			conn.getOutputStream().write(params.toString().getBytes());
			conn.getOutputStream().flush();
		} else {
			super.generateBody();
		}
	}
}
