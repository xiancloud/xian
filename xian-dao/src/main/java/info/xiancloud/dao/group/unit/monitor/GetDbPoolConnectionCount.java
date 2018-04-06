package info.xiancloud.dao.group.unit.monitor;

import com.alibaba.fastjson.JSONArray;
import info.xiancloud.core.Group;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.distribution.unit.ReceiveAndBroadcast;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.falcon.DiyMonitorGroup;
import info.xiancloud.dao.jdbc.pool.PoolFactory;

/**
 * @author happyyangyuan
 */
public class GetDbPoolConnectionCount extends ReceiveAndBroadcast {
    @Override
    public String getName() {
        return "getDbPoolConnectionCount";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("Collect all db nodes' connection pool status.")
                .setPublic(false);
    }

    @Override
    protected UnitResponse execute0(UnitRequest msg) {
        int masterPoolActiveCount = PoolFactory.getPool().getMasterDatasource().getActiveConnectionCount(),
                masterPoolSize = PoolFactory.getPool().getMasterDatasource().getPoolSize(),
                slavePoolActiveCount = PoolFactory.getPool().getSlaveDatasource().getActiveConnectionCount(),
                slavePoolSize = PoolFactory.getPool().getSlaveDatasource().getPoolSize();
        JSONArray monitorBeans = new JSONArray() {{
            add(new DbPoolInfoMonitorBean()
                    .setTitle("DbConnectionPool")
                    .setDatasource(PoolFactory.getPool().getMasterDatasource().getDatabase())
                    .setName("masterPoolActiveCount")
                    .setValue(masterPoolActiveCount)
                    .setNodeId(LocalNodeManager.LOCAL_NODE_ID)
            );
//            add(new DbPoolInfoMonitorBean()
//                    .setTitle("DbConnectionPool")
//                    .setDatasource(PoolFactory.getPool().getMasterDatasource().getDatabase())
//                    .setName("masterPoolSize")
//                    .setValue(masterPoolSize)
//                    .setNodeId(LocalNodeManager.LOCAL_NODE_ID)
//            );
            add(new DbPoolInfoMonitorBean()
                    .setTitle("DbConnectionPool")
                    .setDatasource(PoolFactory.getPool().getMasterDatasource().getDatabase())
                    .setName("slavePoolActiveCount")
                    .setValue(slavePoolActiveCount)
                    .setNodeId(LocalNodeManager.LOCAL_NODE_ID)
            );
//            add(new DbPoolInfoMonitorBean()
//                    .setTitle("DbConnectionPool")
//                    .setDatasource(PoolFactory.getPool().getMasterDatasource().getDatabase())
//                    .setName("slavePoolSize")
//                    .setValue(slavePoolSize)
//                    .setNodeId(LocalNodeManager.LOCAL_NODE_ID)
//            );
        }};
        return UnitResponse.createSuccess(monitorBeans);
    }

    @Override
    protected boolean async() {
        return false;
    }

    @Override
    protected boolean successDataOnly() {
        return true;
    }

    @Override
    public Group getGroup() {
        return DiyMonitorGroup.singleton;
    }
}
