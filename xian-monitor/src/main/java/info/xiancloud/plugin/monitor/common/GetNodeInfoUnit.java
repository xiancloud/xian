package info.xiancloud.plugin.monitor.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.distribution.NodeStatus;
import info.xiancloud.core.distribution.exception.ApplicationOfflineException;
import info.xiancloud.core.distribution.exception.ApplicationUndefinedException;
import info.xiancloud.core.distribution.loadbalance.ApplicationRouter;
import info.xiancloud.core.distribution.service_discovery.ApplicationDiscovery;
import info.xiancloud.core.distribution.service_discovery.ApplicationInstance;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.StringUtil;

import java.util.List;

/**
 * 如果不是为了监控，请使用 {@link info.xiancloud.core.distribution.unit.GetNodeInfoUnit} 本类职责应当是每个节点都可以提供的，而不是仅限于monitor节点。
 * 本类只提供给监控使用
 *
 * @author happyyangyuan
 */
public class GetNodeInfoUnit implements Unit {
    @Override
    public String getName() {
        return "getNodeInfo";
    }

    @Override
    public Group getGroup() {
        return MonitorGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("获取所有节点详细信息");
    }

    @Override
    public Input getInput() {
        return new Input().add("application", String.class, "子系统名称,如果为空，那么不限制查询条件获取所有节点信息", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        if (StringUtil.isEmpty(msg.get("application"))) {
            JSONArray nodeInfoArray = new JSONArray();
            for (String application : ApplicationDiscovery.singleton.queryForNames()) {
                JSONObject nodeInfo;
                try {
                    nodeInfo = nodeInfo(ApplicationRouter.singleton.newestDefinition(application));
                } catch (ApplicationUndefinedException e) {
                    LOG.info("忽略未定义的application：" + application);
                    continue;
                }
                nodeInfoArray.add(nodeInfo);
            }
            return UnitResponse.success(nodeInfoArray);
        } else try {
            JSONArray nodeInfoArray = new JSONArray();
            List<ApplicationInstance> clients = ApplicationRouter.singleton.allInstances(msg.get("application"));
            for (ApplicationInstance instance : clients) {
                nodeInfoArray.add(nodeInfo(instance.getPayload()));
            }
            return UnitResponse.success(nodeInfoArray);
        } catch (ApplicationOfflineException | ApplicationUndefinedException e) {
            return e.toUnitResponse();
        }
    }

    private JSONObject nodeInfo(NodeStatus status) {
        return Reflection.toType(status, JSONObject.class);
    }
}
