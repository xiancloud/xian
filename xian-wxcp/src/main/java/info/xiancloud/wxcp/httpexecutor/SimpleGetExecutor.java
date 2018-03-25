package info.xiancloud.wxcp.httpexecutor;


import info.xiancloud.core.util.http.HttpKit;
import info.xiancloud.wxcp.exception.WxError;
import info.xiancloud.wxcp.exception.WxErrorException;

/**
 * 简单型的get请求执行器
 * 
 * @author yyq
 *
 */
public class SimpleGetExecutor implements WxReqExecutor<String, String> {

	@Override
	public String execute(String uri, String data) throws WxErrorException, Exception {

		String responseContent = HttpKit.get(uri).setContent(data).setSSL(null, null).execute();
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
