package info.xiancloud.core.support.cos;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SingleRxXian;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.HashMap;
import java.util.Map;

/**
 * 云文件存储操作
 *
 * @author happyyangyuan
 */
public class CloudFile {
    /**
     * 保存文件到云上，阻塞直到保存成功返回
     *
     * @param path 文件相对路径
     * @param data 文件内容
     * @return successful/unsuccessful unit response
     */
    public static Completable save(String path, String data) {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("path", path);
            put("data", data);
        }};
        return SingleRxXian.call("cosService", "cosWrite", map).toCompletable();
    }

    /**
     * 分N线程并行上传云文件，线程数默认值是经过本地验证取最优的
     *
     * @param pathDataMap 批量数据,key为path，value为文件内容
     * @return completable
     */
    public static Completable save(Map pathDataMap) {
        return SingleRxXian.call("cosService", "batchCosWrite", new JSONObject() {{
            put("files", pathDataMap);
        }}).toCompletable();
    }

    /**
     * 分N线程并行上传云文件
     *
     * @param pathDataMap 批量数据,key为path，value为文件内容
     * @param threadCount 并行数，如果你不知道该使用多大的线程数，请只使用具有默认值的{@linkplain #save(Map)}
     * @return completable
     */
    public static Completable save(Map pathDataMap, int threadCount) {
        return SingleRxXian.call("cosService", "batchCosWrite", new JSONObject() {{
            put("files", pathDataMap);
            put("threadCount", threadCount);
        }}).toCompletable();
    }

    /**
     * 从云端读取文件
     *
     * @param path 文件相对路径
     * @return 文件内容
     */
    public static Single<String> read(String path) {
        return SingleRxXian.call("cosService", "cosRead", new JSONObject() {{
            put("path", path);
        }}).flatMap(response -> {
            String str = response.dataToStr();
            if (response.succeeded() && str != null)
                return Single.just(str);
            else
                return Single.error(response.getException());
        });
    }

    /**
     * check whether the cos file exists
     *
     * @param path the cos path
     * @return true if exits, false if not exits, error on exception.
     */
    public static Single<Boolean> exists(String path) {
        return SingleRxXian.call("cosService", "cosCheckFileExists", new JSONObject() {{
            put("path", path);
        }}).flatMap(response -> Single.just(response.dataToBoolean()));
    }
}
