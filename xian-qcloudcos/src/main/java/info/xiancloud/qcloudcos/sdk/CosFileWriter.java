package info.xiancloud.qcloudcos.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.meta.InsertOnly;
import com.qcloud.cos.request.GetFileInputStreamRequest;
import com.qcloud.cos.request.UploadFileRequest;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.io.StringIO;

import java.io.InputStream;
import java.util.Objects;

/**
 * 本类对本地io有依赖，请注意
 *
 * @author happyyangyuan
 */
public class CosFileWriter extends AbsFileOperate {

    public boolean forPath(String path, String data) {
        try {
            checkPath(path);
            getCosClient();//初始化cosClient，如果没有初始化的话
            UploadFileRequest request = new UploadFileRequest(BUCKET, getBase() + path, data.getBytes());
            request.setInsertOnly(InsertOnly.OVER_WRITE);
            return doUpload(request, 3);
        } catch (Throwable e) {
            LOG.error(e);
            return false;
        }
    }

    private boolean doUpload(UploadFileRequest uploadFileRequest, int maxRetry) {
        System.out.println(maxRetry);
        maxRetry--;
        String uploadFileRet = cosClient.uploadFile(uploadFileRequest);
        LOG.info("上传文件返回：" + uploadFileRet);
        boolean succeeded = succeeded(uploadFileRet);
        if (succeeded) {
            return true;
        } else {
            if (maxRetry <= 0) {
                return false;
            } else {
                return doUpload(uploadFileRequest, maxRetry);
            }
        }
    }

    private boolean succeeded(String cosOperateReturn) {
        JSONObject parsed = JSON.parseObject(cosOperateReturn);
        Object code = parsed.get("code");
        if (Objects.equals("0", code) || Objects.equals(0, code)) {
            return true;
        }
        return false;
    }

    class FileExistedException extends Exception {
        String path;

        FileExistedException(String path) {
            this.path = path;
        }

        @Override
        public String getMessage() {
            return path + " 已存在";
        }
    }

    public static void main(String[] args) {
        COSClient cosClient = new COSClient(10053621, "AKID5iJcsYewRYIJhqQsoaLQ7Ks1XIO6eYPs", "Gm0nqHOPzUG1MRRJnBLX4UwwQMoh8v4y");
        ClientConfig config = new ClientConfig();
        config.setRegion("sh");
        cosClient.setConfig(config);
        UploadFileRequest request = new UploadFileRequest("xian", "/xian_runtime_IDE_happyyangyuan/xian/yy/991.txt", "测试data".getBytes());
        request.setInsertOnly(InsertOnly.OVER_WRITE);
        String uploadFileRet = cosClient.uploadFile(request);
        LOG.info("上传文件返回：" + uploadFileRet);


        GetFileInputStreamRequest inputStreamRequest = new GetFileInputStreamRequest("xian", "/xian_runtime_IDE_happyyangyuan/xian/yy/991.txt");
        inputStreamRequest.setUseCDN(false);
        try (InputStream inputStream = cosClient.getFileInputStream(inputStreamRequest)) {
            System.out.println("读文件：" + StringIO.readFully(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
