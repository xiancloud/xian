package info.xiancloud.core.support.cos;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.util.LOG;

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
     * @return true成功 ；false失败
     */
    public static boolean save(String path, String data) {
        Map map = new HashMap<String, Object>() {{
            put("path", path);
            put("data", data);
        }};
        return SyncXian.call("cosService", "cosWrite", map).succeeded();
    }

    /**
     * @param path  文件相对路径
     * @param data  文件内容
     * @param async 是否异步执行,如果不清楚，请使用{@linkplain CloudFile#save(String, String)}
     */
    public static void save(String path, String data, boolean async) {
        Map map = new HashMap<String, Object>() {{
            put("path", path);
            put("data", data);
        }};
        if (async) {
            Xian.call("cosService", "cosWrite", map, new NotifyHandler() {
                @Override
                protected void handle(UnitResponse unitResponse) {
                    LOG.info(unitResponse);
                }
            });
        } else {
            SyncXian.call("cosService", "cosWrite", map);
        }
    }

    /**
     * 分N线程并行上传云文件，线程数默认值是经过本地验证取最优的
     *
     * @param pathDataMap 批量数据,key为path，value为文件内容
     */
    public static void save(Map pathDataMap) {
        SyncXian.call("cosService", "batchCosWrite", new JSONObject() {{
            put("files", pathDataMap);
        }});
    }

    /**
     * 分N线程并行上传云文件
     *
     * @param pathDataMap 批量数据,key为path，value为文件内容
     * @param threadCount 并行数，如果你不知道该使用多大的线程数，请只使用具有默认值的{@linkplain #save(Map)}
     */
    public static void save(Map pathDataMap, int threadCount) {
        SyncXian.call("cosService", "batchCosWrite", new JSONObject() {{
            put("files", pathDataMap);
            put("threadCount", threadCount);
        }});
    }

    /**
     * 从云端读取文件
     *
     * @param path 文件相对路径
     * @return 文件内容
     */
    public static String read(String path) {
        return SyncXian.call("cosService", "cosRead", new JSONObject() {{
            put("path", path);
        }}).dataToStr();
    }

    public static boolean exists(String path) {
        return SyncXian.call("cosService", "cosCheckFileExists", new JSONObject() {{
            put("path", path);
        }}).dataToBoolValue();
    }
}
