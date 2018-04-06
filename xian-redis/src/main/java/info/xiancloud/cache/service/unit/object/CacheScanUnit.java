package info.xiancloud.cache.service.unit.object;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.cache.CacheConfigBean;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * Scan
 * https://redis.io/commands/scan
 *
 * @author John_zero, happyyangyuanF
 */
public class CacheScanUnit implements Unit {
    @Override
    public String getName() {
        return "cacheScan";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Scan").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("pattern", String.class, "", REQUIRED)
                .add("count", int.class, "", NOT_REQUIRED)
                .add("cursor", String.class, "", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    private static final int THRESHOLD_VALUE = 200;

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String pattern = msg.getArgMap().get("pattern").toString();
        int count = msg.get("count", Integer.class, THRESHOLD_VALUE);
        String cursor = msg.getArgMap().get("cursor").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);
        try {
            JSONObject jsonObject = Redis.call(cacheConfigBean, jedis -> {
                JSONObject _jsonObject = new JSONObject();
                ScanParams params = new ScanParams().match(pattern).count(count);
                ScanResult<String> scans = jedis.scan(cursor, params);
                _jsonObject.put("cursor", scans.getStringCursor());// 如果服务器向客户端返回 0 的游标时则表示迭代结束
                _jsonObject.put("result", scans.getResult());
                return _jsonObject;
            });
            handler.handle(UnitResponse.createSuccess(jsonObject));
        } catch (Exception e) {
            handler.handle(UnitResponse.createException(e));
        }
    }

}
