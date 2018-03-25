package info.xiancloud.core.support.transaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.exception.UnitOfflineException;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.Constant;
import info.xiancloud.core.Unit;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.exception.UnitOfflineException;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.distribution.service_discovery.UnitInstance;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * json格式参考文件:    redis内存放的事务元数据.json
 * todo 考虑将这个分布式事务工具类迁移到db插件内
 * todo 有bug需要修复
 *
 * @author happyyangyuan
 */
public class TransactionalCache {

    //分布式事务信息的缓存
    /*private static final CacheMap<String, String> transMetas = new CacheMap<>("transId-transMeta");*/
    private static final Map<String, String> transMetas = new ConcurrentHashMap<>();//临时不做分布式事务

    public static boolean exists() {
        return transMetas.get(MsgIdHolder.get()) != null;
    }

    /**
     * 开启分布式事务
     */
    public static void beginDistributedTrans() {
        increaseCount();
    }

    public static void commitDistributedTrans() {
        decreaseCount();
        JSONArray clientIds = JSON.parseObject(transMetas.get(MsgIdHolder.get())).getJSONArray("clientIds");
        if (clientIds != null && !clientIds.isEmpty()) {
            String clientId = clientIds.getString(0);
            UnitRequest request = UnitRequest.create(Constant.SYSTEM_DAO_GROUP_NAME, "commitAndCloseTogether");
            request.getContext().setDestinationNodeId(clientId);
            LocalNodeManager.send(request, new NotifyHandler() {
                protected void handle(UnitResponse unitResponse) {
                    LOG.info(clientId + "的事务已经提交");
                }
            });
        } else {
            LOG.warn("提交事务时,缓存中的事务信息竟然是空的!", new RuntimeException());
        }
    }

    public static void rollbackDistributedTrans() {
        JSONArray clientIds = JSON.parseObject(transMetas.get(MsgIdHolder.get())).getJSONArray("clientIds");
        if (clientIds != null && !clientIds.isEmpty()) {
            String clientId = clientIds.getString(0);
            UnitRequest request = UnitRequest.create(Constant.SYSTEM_DAO_GROUP_NAME, "rollbackAndCloseTogether");
            request.getContext().setDestinationNodeId(clientId);
            LocalNodeManager.send(request, new NotifyHandler() {
                protected void handle(UnitResponse unitResponse) {
                    LOG.info(clientId + "的事务已经回滚");
                }
            });
        } else {
            LOG.warn("回滚事务时,缓存中的事务信息竟然是空的!", new RuntimeException());
        }
    }

    /**
     * 为分布式事务缓存新增一个clientId,本方法只允许db进程调用;如果当前还不存在分布式事务,那么创建一个.
     */
    public static void addLocalDbClient() {
        if (!EnvUtil.isDao() && !EnvUtil.isIDE()) {
            throw new RuntimeException("只允许db进程调用本方法");
        }
        JSONObject cachedTrans = JSON.parseObject(transMetas.get(MsgIdHolder.get()));
        if (cachedTrans == null) {
            cachedTrans = new JSONObject() {{
                put("count", 0);
            }};
        }
        JSONArray clientIds = cachedTrans.getJSONArray("clientIds");
        if (clientIds == null) {
            clientIds = new JSONArray();
            cachedTrans.put("clientIds", clientIds);
        }
        clientIds.add(LocalNodeManager.LOCAL_NODE_ID);
        transMetas.put(MsgIdHolder.get(), cachedTrans.toJSONString());
    }

    public static int getCount() {
        JSONObject cachedTrans = JSON.parseObject(transMetas.get(MsgIdHolder.get()));
        return cachedTrans.getIntValue("count");
    }

    /**
     * 实时从缓存内内获取事务所在的clientId，如果没有开启过事务，那么返回null
     */
    public static String getCachedTransactionalDbClientId(String group, String unit) throws UnitOfflineException, UnitUndefinedException {
        JSONObject cachedTrans = JSON.parseObject(transMetas.get(MsgIdHolder.get()));
        if (cachedTrans != null) {
            JSONArray clientIds = cachedTrans.getJSONArray("clientIds");
            for (UnitInstance clientInfo : UnitRouter.singleton.allInstances(Unit.fullName(group, unit))) {
                for (Object dbClientId : clientIds) {
                    if (Objects.equals(dbClientId, clientInfo.getNodeId())) {
                        return dbClientId.toString();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 为分布式事务计数器加一,如果当前不存在分布式事务,那么创建一个,并开启之
     */
    public static void increaseCount() {
        JSONObject cachedTrans = JSON.parseObject(transMetas.get(MsgIdHolder.get()));
        if (cachedTrans == null) {
            cachedTrans = new JSONObject() {{
                put("count", 0);
                put("clientIds", new JSONArray());
            }};
        }
        cachedTrans.put("count", cachedTrans.getIntValue("count") + 1);
        transMetas.put(MsgIdHolder.get(), cachedTrans.toJSONString());
    }

    public static void decreaseCount() {
        JSONObject cachedTrans = JSON.parseObject(transMetas.get(MsgIdHolder.get()));
        if (cachedTrans != null) {
            cachedTrans.put("count", cachedTrans.getIntValue("count") - 1);
            transMetas.put(MsgIdHolder.get(), cachedTrans.toJSONString());
        } else {
            LOG.warn("事务id" + MsgIdHolder.get() + "对应的缓存不存在");
        }
    }

    /**
     * 删除事务缓存,并返回dbClientId列表
     */
    public static JSONArray clear() {
        JSONObject cachedTrans = JSON.parseObject(transMetas.remove(MsgIdHolder.get()));
        if (cachedTrans != null) {
            if (cachedTrans.getIntValue("count") != 0) {
                LOG.warn("事务未正常结束就清空了事务cache");
            }
            return cachedTrans.getJSONArray("clientIds");
        } else {
            LOG.warn("事务id" + MsgIdHolder.get() + "对应的缓存不存在");
            return new JSONArray();
        }
    }

}
