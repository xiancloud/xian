package info.xiancloud.plugin.distribution.exception;

import info.xiancloud.plugin.message.id.NodeIdBean;

/**
 * node is offline exception.
 *
 * @author happyyangyuan
 */
public class ApplicationInstanceOfflineException extends ApplicationOfflineException {

    private String nodeId;

    public ApplicationInstanceOfflineException(String nodeId) {
        super(NodeIdBean.parse(nodeId).getApplication());
        this.nodeId = nodeId;
    }

    @Override
    public String getMessage() {
        return "目标节点不在线：" + nodeId;
    }
}
