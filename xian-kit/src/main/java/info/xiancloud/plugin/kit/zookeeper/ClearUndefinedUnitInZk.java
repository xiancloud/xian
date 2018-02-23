package info.xiancloud.plugin.kit.zookeeper;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.kit.KitService;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;
import info.xiancloud.plugin.zookeeper.ZkConnection;

/**
 * @author happyyangyuan
 */
public class ClearUndefinedUnitInZk implements Unit {
    @Override
    public String getName() {
        return "clearUndefinedUnitInZk";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("清除掉未定义的zk内的脏unit，同时也支持清理group脏节点")
                .setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("basePath", String.class, "unit、group根路径，不允许以'/'结尾，eg. /xian_runtime_dev/unit", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        /**
         * 请设置此变量来删除指定路径下的脏节点
         */
        final String PATH = msg.get("basePath", "/xian_runtime_dev/unit");
        try {
            ZkConnection.start();
            for (String s : ZkConnection.client.getChildren().forPath(PATH)) {
                String fullPath = PATH.concat("/").concat(s);
                String data = new String(ZkConnection.client.getData().forPath(fullPath));
                System.out.println(data);
                if (StringUtil.isEmpty(data)) {
                    LOG.debug("实现原理是xian服务注册会在unit和group节点data上写入其定义数据，如果没有定义数据的，那么一定是脏节点");
                    ZkConnection.client.delete().forPath(fullPath);
                }
            }
        } catch (Throwable e) {
            return UnitResponse.exception(e);
        } finally {
            ZkConnection.close();
        }
        return UnitResponse.success();
    }

    @Override
    public Group getGroup() {
        return KitService.singleton;
    }

}
