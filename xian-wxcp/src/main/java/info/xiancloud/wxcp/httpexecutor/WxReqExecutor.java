package info.xiancloud.wxcp.httpexecutor;

import java.io.IOException;

import info.xiancloud.wxcp.exception.WxErrorException;

/**
 * 微信http消息发送器
 * 
 * @author yyq
 *
 * @param <T> 返回值类型
 * @param <E>请求参数类型
 */
public interface WxReqExecutor<T, E> {

	 /**
	   * @param uri        请求的uri (允许带accessToken等公共参数)
	   * @param data       发送的数据
	   * @throws WxErrorException
	   * @throws IOException
	   */
	  T execute(String uri, E data) throws WxErrorException, IOException,Exception;

}
