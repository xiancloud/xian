package info.xiancloud.wxcp.httpexecutor;

import java.io.IOException;

import info.xiancloud.wxcp.exception.WxError;
import info.xiancloud.wxcp.exception.WxErrorException;
import info.xiancloud.core.util.http.HttpKit;

/**
 * 简单型的Post请求执行器
 * 
 * @author yyq
 *
 */
public class SimplePostExecutor implements WxReqExecutor<String, String> {

	@Override
	public String execute(String uri, String data) throws WxErrorException, IOException, Exception {

		String responseContent = HttpKit.post(uri).setContent(data).setSSL(null, null).execute();
		if (responseContent.isEmpty()) {
			throw new WxErrorException(WxError.newBuilder().setErrorCode(9999).setErrorMsg("无响应内容").build());
		}
		if (responseContent.startsWith("<xml>")) {
			// xml格式输出直接返回
			return responseContent;
		}
		WxError error = WxError.fromJson(responseContent);
		if (error.getErrorCode() != 0) {
			throw new WxErrorException(error);
		}
		return responseContent;
	}
}
