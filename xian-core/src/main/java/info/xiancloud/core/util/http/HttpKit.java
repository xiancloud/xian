package info.xiancloud.core.util.http;

/**
 * HTTP(s)请求工具类
 * 
 * @author yyq
 *
 */
public class HttpKit {

	public static Request get(String url) {
		return new GetRequest(url);
	}

	public static Request post(String url) {
		return new PostRequest(url);
	}

	public static Request delelte(String url) {
		return new DeleteRequest(url);
	}

	public static Request put(String url) {
		return new PutRequest(url);
	}
}
