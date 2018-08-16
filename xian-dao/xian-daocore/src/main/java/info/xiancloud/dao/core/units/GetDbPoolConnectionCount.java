package info.xiancloud.dao.core.units;

import com.alibaba.fastjson.JSONArray;
import info.xiancloud.core.*;
import info.xiancloud.core.distribution.LocalNodeManager;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.falcon.DiyMonitorGroup;
import info.xiancloud.dao.core.model.monitor.DbPoolInfoMonitorBean;
import info.xiancloud.dao.core.pool.PoolFactory;

/**
 * For database pooling client monitoring
 *
 * @author happyyangyuan
 */
public class GetDbPoolConnectionCount implements Unit {
    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public String getName() {
        return "getDbPoolConnectionCount";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta
                .createWithDescription("Collect all db nodes' connection pool status.")
                .setDocApi(false)
                .setBroadcast()
                ;
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) {
        handler.handle(execute0(request));
    }

    private UnitResponse execute0(UnitRequest request) {
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
    public Group getGroup() {
        return DiyMonitorGroup.singleton;
    }
}
