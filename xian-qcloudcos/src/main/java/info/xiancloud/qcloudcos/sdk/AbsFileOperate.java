package info.xiancloud.qcloudcos.sdk;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import info.xiancloud.core.util.EnvUtil;

import java.io.File;

/**
 * @author happyyangyuan
 */
abstract class AbsFileOperate {
    COSClient cosClient;
    String BUCKET = "xian";

    public void close() {
        if (cosClient != null)
            cosClient.shutdown();
    }

    String getBase() {
        return "/" + EnvUtil.getEnv() + "/";
    }

    String url(String path) {
        if (EnvUtil.isQcloudLan()) {
            return "http://xian-10053621.innercos.myqcloud.com" + getBase() + path;
        } else {
            return "http://xian-10053621.file.myqcloud.com" + getBase() + path;
            /*return "http://xian-10053621.cos.myqcloud.com" + getMeta() + path;*/
        }
    }

    public COSClient getCosClient() {
        if (cosClient == null) {
            cosClient = new COSClient(10053621, "AKID5iJcsYewRYIJhqQsoaLQ7Ks1XIO6eYPs", "Gm0nqHOPzUG1MRRJnBLX4UwwQMoh8v4y");
            ClientConfig config = new ClientConfig();
            config.setRegion("sh");
            cosClient.setConfig(config);
        }
        return cosClient;
    }

    void checkPath(String path) {
        if (path.startsWith("/"))
            throw new IllegalArgumentException(path + " 应当是相对路径，不允许以'/'开头");
    }

    /**
     * 被腾讯云sdk恶心到了，cos_api的sdk依赖本地真实存在的文件，意味着以后xian的cos组件必须部署至本地IO足够快的服务器上.（我收回这个吐槽 2017-04-30）
     *
     * @deprecated 升级后的文件上传sdk，支持直接将字节数组发送到远程，不需要写本地临时文件了
     */
    String generateLocalTmpFilePath(String path) {
        new File("tmp/cos").mkdirs();
        String localTmpFile = "tmp/cos/" + System.currentTimeMillis() + "-" + path.hashCode();
        return localTmpFile;
    }

}
