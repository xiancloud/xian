package info.xiancloud.apidoc.handler;

/**
 * 构建完成回调
 * 
 * @author yyq
 *
 */
public interface BuildCallback {

	/**
	 * 文件内容byte
	 * 
	 * @param data
	 */
	public void call(byte[] data);
}
