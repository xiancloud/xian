package info.xiancloud.qcloudcos.api.http;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DefaultQCloudCosHttpClient extends QCloudCosHttpClient {

	@Override
	protected Response sendGetRequest(HttpRequest req) throws IOException {

		Request.Builder builder = new Request.Builder();
		fillHeader(req, builder);

		Request request = builder.url(req.getUrl()).get().build();
		Response response = client.newCall(request).execute();
		return response;
	}

	@Override
	protected Response sendPostRequest(HttpRequest req) {

		return null;
	}

	@Override
	protected Response sendPutRequest(HttpRequest req) throws IOException {

		RequestBody reqeustBody = RequestBody.create(MediaType.parse(req.getContentType().toString()), req.getBytes());

		Request.Builder builder = new Request.Builder();
		fillHeader(req, builder);

		Request request = builder.url(req.getUrl()).put(reqeustBody).build();
		Response response = client.newCall(request).execute();
		return response;

	}

	@Override
	protected Response sendDeleteRequest(HttpRequest request) {

		return null;
	}

	private void fillHeader(HttpRequest xianRequest, Request.Builder builder) {
		if (!xianRequest.getHeaders().isEmpty()) {
			xianRequest.getHeaders().forEach((k, v) -> {
				builder.header(k, v);
			});
		}
	}
}
