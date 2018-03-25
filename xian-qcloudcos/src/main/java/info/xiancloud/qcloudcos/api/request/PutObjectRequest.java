package info.xiancloud.qcloudcos.api.request;

import java.io.InputStream;
import java.util.TreeMap;

import info.xiancloud.qcloudcos.api.QCloudCosConfig;
import info.xiancloud.qcloudcos.api.http.HttpContentType;
import info.xiancloud.qcloudcos.api.http.HttpMethod;
import info.xiancloud.qcloudcos.api.http.HttpRequest;

/**
 * object上传请求
 * 
 * @author yyq
 *
 */
public class PutObjectRequest extends BaseRequest {

	private byte[] contentBufer;
	private InputStream inputStream;
	// 是否是byte 上传
	private boolean uploadFromBuffer = false;

	public PutObjectRequest(String bucketName, String cosPath, QCloudCosConfig config, byte[] contentBuffer) {
		super(bucketName, cosPath, config);
		this.contentBufer = contentBuffer;
		this.uploadFromBuffer = true;
		this.httpMethod = HttpMethod.PUT;
		this.contentBufer = contentBuffer;
	}

	public PutObjectRequest(String bucketName, String cosPath, QCloudCosConfig config, InputStream inputStream) {
		super(bucketName, cosPath, config);
		this.inputStream = inputStream;
	}

	@Override
	public HttpRequest buildHttpReqest() {
		String url = buildUrl();
		String auth = buildAuthorization();
		HttpRequest request = new HttpRequest();
		request.setUrl(url);
		request.setBytes(contentBufer);
		request.setMethod(httpMethod);
		request.setContentType(HttpContentType.OCTET_STREAM);
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

	public static void main(String[] args) {

		PutObjectRequest fileRequest = new PutObjectRequest("testbucket", "/testfile", new QCloudCosConfig(),
				new byte[1]);
		fileRequest.buildHttpReqest();
	}
}
