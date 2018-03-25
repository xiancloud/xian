package info.xiancloud.qcloudcos.api.request;

import java.io.InputStream;
import java.util.TreeMap;

import info.xiancloud.qcloudcos.api.QCloudCosConfig;
import info.xiancloud.qcloudcos.api.http.HttpContentType;
import info.xiancloud.qcloudcos.api.http.HttpMethod;
import info.xiancloud.qcloudcos.api.http.HttpRequest;

/**
 * 下载object请求
 * 
 * @author yyq
 *
 */
public class GetObjectRequest extends BaseRequest {

	private byte[] contentBufer;
	private InputStream inputStream;
	// 是否是byte 上传
	private boolean uploadFromBuffer = false;

	public GetObjectRequest(String bucketName, String cosPath, QCloudCosConfig config) {
		super(bucketName, cosPath, config);
		this.httpMethod = HttpMethod.GET;
	}


	@Override
	public HttpRequest buildHttpReqest() {
		String url = buildUrl();
		String auth = buildAuthorization();
		HttpRequest request = new HttpRequest();
		request.setUrl(url);
		request.setMethod(httpMethod);
		request.setContentType(HttpContentType.TEXT_PLAIN);
		request.addHeader("Authorization", auth);
		return request;
	}

	@Override
	protected TreeMap<String, String> signHeader() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("host", host);
		return map;
	}

	@Override
	protected TreeMap<String, String> signParams() {
		return null;
	}

	public byte[] getContentBufer() {
		return contentBufer;
	}

	public void setContentBufer(byte[] contentBufer) {
		this.contentBufer = contentBufer;
	}

	public boolean isUploadFromBuffer() {
		return uploadFromBuffer;
	}

	public void setUploadFromBuffer(boolean uploadFromBuffer) {
		this.uploadFromBuffer = uploadFromBuffer;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
}
