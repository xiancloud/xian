package info.xiancloud.qcloudcos.sdk;

import com.qcloud.cos.request.GetFileInputStreamRequest;
import info.xiancloud.core.util.io.StringIO;

import java.io.InputStream;

/**
 * @author happyyangyuan
 */
public class CosFileReader extends AbsFileOperate {

    public boolean exists(String path) {
        checkPath(path);
        getCosClient();
        GetFileInputStreamRequest inputStreamRequest = new GetFileInputStreamRequest(BUCKET, getBase() + path);
        try (InputStream ignored = cosClient.getFileInputStream(inputStreamRequest)) {
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String forPath(String path) {
        checkPath(path);
        getCosClient();
        GetFileInputStreamRequest inputStreamRequest = new GetFileInputStreamRequest(BUCKET, getBase() + path);
        inputStreamRequest.setUseCDN(false);//todo 后面可以在v4全面开放之后使用默认开启
        try (InputStream inputStream = cosClient.getFileInputStream(inputStreamRequest)) {
            return StringIO.readFully(inputStream);
        } catch (Exception e) {
            throw new RemoteFileReadFailed(path, e);
        }
    }

    class RemoteFileReadFailed extends RuntimeException {
        String path;

        RemoteFileReadFailed(String path) {
            this.path = path;
        }

        RemoteFileReadFailed(String path, Throwable cause) {
            super(cause);
            this.path = path;
        }

        public String getMessage() {
            return path + " 读取失败";
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            long start = System.currentTimeMillis();
            System.out.println(new CosFileReader().forPath("yy/991.txt"));
//            System.out.println(new CosFileReader().exists("yy/991.txt"));
        }
    }
}
