package info.xiancloud.apidoc.handler;

/**
 * 构建完成回调
 *
 * @author yyq
 */
public interface BuildCallback {

    /**
     * 文件内容byte
     *
     * @param data the api content byte array
     */
    void call(byte[] data);
}
