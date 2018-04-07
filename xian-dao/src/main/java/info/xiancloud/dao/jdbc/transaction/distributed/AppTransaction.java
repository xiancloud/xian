package info.xiancloud.dao.jdbc.transaction.distributed;

import com.alibaba.fastjson.JSONArray;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.transaction.TransactionalCache;
import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.jdbc.pool.PoolFactory;
import info.xiancloud.dao.jdbc.transaction.ReentrantTransaction;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用层启动的事务对象,可跨进程(JVM虚拟机)开始和提交事务,可跨数据源的事务.
 * 目前依赖redis全局缓存机制
 *
 * @author happyyangyuan
 */
public class AppTransaction extends ReentrantTransaction implements IDistributedTransaction, Serializable {

    private final Date createDate = new Date();
    private final static Map<Object, AppTransaction> localTransMap = new ConcurrentHashMap<>();

    private AppTransaction(Object transactionId) {
        super(PoolFactory.getPool().getWriteConnection(), transactionId);
    }

    /**
     * 创建应用层事务,如果已存在应用层事务那么直接返回那个已存在的,否则构造一个
     */
    public static AppTransaction createTransaction(Object transId) {
        if (localTransMap.get(transId) != null) {
            LOG.debug("Transaction is already begun. Joined to existed transaction only.");
            return localTransMap.get(transId);
        }
        LOG.debug("正在新建appTransaction事务,并初始化事务id为" + transId);
        TransactionalCache.addLocalDbClient();
        AppTransaction trans = new AppTransaction(transId);
        localTransMap.put(transId, trans);
        LOG.debug("appTransaction创建完毕,transId=" + transId);
        return trans;
    }

    /**
     * 获取已存在的应用层事务,如果不存在应用层事务, 那么返回null;
     * 如果已经存在分布式事务,那么在本地新建一个本地事务.
     */
    public static AppTransaction getExistedAppTrans(Object transId) {
        if (localTransMap.get(transId) != null) {
            return localTransMap.get(transId);
        } else if (TransactionalCache.exists()) {
            return createTransaction(transId);
        } else {
            return null;
        }
    }

    public Date getCreateDate() {
        return createDate;
    }

    @Override
    protected void clear() {
        LOG.debug("我们要求所有的分布式事务在最终一起清理,而不是分别清理.这里什么也不用做");
    }

    public static Map<Object, AppTransaction> getLocalTransMap() {
        return localTransMap;
    }

    @Override
    protected void doClose() throws SQLException {
        LOG.debug("我们要求分布式事务在业务结束时一起提交和关闭数据库连接");
        if (rollbacked) {
            rollbackAndCloseTogether();
        } else {
            if (TransactionalCache.getCount() == 0) {
                commitAndCloseTogether();
            }
        }
    }

    @Override
    protected void doBegin() throws SQLException {
        TransactionalCache.increaseCount();
        super.doBegin();
    }

    @Override
    protected void doCommit() /*throws SQLException*/ {
        //这里并不提交,这里只是将缓存内事务计数器减一
        TransactionalCache.decreaseCount();
    }

    protected void doRollback()/* throws SQLException */ {
        LOG.debug("对于分布式事务,我们要求所有事务一起回滚,父类会将rollbacked标记为true,待连关闭连接时,会自动触发所有事务回滚");
    }

    @Override
    public void commitAndCloseTogether() throws SQLException {
        //发送远程请求,将其他数据库事务提交和关闭
        JSONArray clientIds = TransactionalCache.clear();
        if (clientIds != null) {
            for (int i = 0; i < clientIds.size(); i++) {
                final String clientId = clientIds.getString(i);
                if (!LocalNodeManager.LOCAL_NODE_ID.equals(clientId)) {
                    UnitRequest request = UnitRequest.create(CommitAndCloseTogetherUnit.class);
                    request.getContext().setDestinationNodeId(clientId);
                    LocalNodeManager.send(request, new NotifyHandler() {
                        protected void handle(UnitResponse unitResponse) {
                            LOG.info(clientId + "的事务已经提交");
                        }
                    });
                }
            }
        }
        //提交和关闭自己的数据库事务
        connection.commit();
        connection.close();
        //清除本地事务
        localTransMap.remove(transactionId);
    }

    public void rollbackAndCloseTogether() throws SQLException {
        //发送远程请求,将其他数据库事务提交和关闭
        JSONArray clientIds = TransactionalCache.clear();
        if (clientIds != null) {
            for (int i = 0; i < clientIds.size(); i++) {
                final String clientId = clientIds.getString(i);
                if (!LocalNodeManager.LOCAL_NODE_ID.equals(clientId)) {
                    UnitRequest request = UnitRequest.create(RollbackAndCloseTogetherUnit.class);
                    request.getContext().setDestinationNodeId(clientId);
                    LocalNodeManager.send(request, new NotifyHandler() {
                        protected void handle(UnitResponse unitResponse) {
                            LOG.info(clientId + "的事务已经回滚");
                        }
                    });
                }
            }
        }
        //回滚和关闭自己的数据库事务
        connection.rollback();
        connection.close();
        //清除本地事务
        localTransMap.remove(transactionId);
    }

}
