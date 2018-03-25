package info.xiancloud.qcloudcos.api.server;

import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * 请求解析
 * 
 * @author yyq
 *
 */
public class RequestParser {

	/**
	 * 获取url中的参数
	 * 
	 * @return
	 */
	public static Map<String, String> urlParams(FullHttpRequest request) {
		Map<String, String> params = new HashMap<String, String>();

		QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
		decoder.parameters().entrySet().forEach(entry -> {
			params.put(entry.getKey(), entry.getValue().get(0));
		});
		return params;
	}
}
