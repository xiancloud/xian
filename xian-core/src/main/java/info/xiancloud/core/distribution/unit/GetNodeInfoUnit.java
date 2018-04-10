package info.xiancloud.core.distribution.unit;

import info.xiancloud.core.*;
import info.xiancloud.core.distribution.exception.ApplicationOfflineException;
import info.xiancloud.core.distribution.exception.ApplicationUndefinedException;
import info.xiancloud.core.distribution.loadbalance.ApplicationRouter;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;

/**
 * @author happyyangyuan
 */
public class GetNodeInfoUnit implements Unit {
    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("获取所有节点详细信息")
                .setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("application", String.class, "子系统名称,如果为空，那么不限制查询条件获取所有节点信息");
    }

    @Override
    public String getName() {
        return "getNodeInfo";
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> consumer) {
        if (StringUtil.isEmpty(msg.get("application"))) {
            throw new RuntimeException("重构了服务注册，暂时不支持不指定application名称");
        }
        try {
            consumer.handle(UnitResponse.createSuccess(ApplicationRouter.singleton.allInstances(msg.get("application", String.class))));
        } catch (ApplicationOfflineException | ApplicationUndefinedException e) {
            consumer.handle(UnitResponse.createUnknownError(null, "找不到application:" + msg.get("application", String.class)));
        }
    }

    @Override
    public Group getGroup() {
        return SystemGroup.singleton;
    }
}
