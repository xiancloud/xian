package info.xiancloud.plugin.qcloud_cos.api;

import info.xiancloud.plugin.qcloud_cos.api.http.DefaultQCloudCosHttpClient;
import info.xiancloud.plugin.qcloud_cos.api.http.QCloudCosHttpClient;
import info.xiancloud.plugin.qcloud_cos.api.request.GetObjectRequest;
import info.xiancloud.plugin.qcloud_cos.api.request.PutObjectRequest;

/**
 * 腾讯云cos操作客户端
 * 
 * @author yyq
 *
 */
public class QCloudCosClient {

	private final static QCloudCosHttpClient httpClient = new DefaultQCloudCosHttpClient();

	public static String putObject(PutObjectRequest request) {
		String result = httpClient.sendHttpRequest(request.buildHttpReqest(), String.class);
		return result;
	}

	public static byte[] getObject(GetObjectRequest request) {
		byte[] bytes = httpClient.sendHttpRequest(request.buildHttpReqest(), byte[].class);
		return bytes;
	}

}
