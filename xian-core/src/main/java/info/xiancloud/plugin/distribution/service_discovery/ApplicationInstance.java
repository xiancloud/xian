package info.xiancloud.plugin.distribution.service_discovery;

import info.xiancloud.plugin.distribution.NodeStatus;

import java.util.Objects;

/**
 * For Service administration.
 *
 * @author happyyangyuan
 */
public class ApplicationInstance extends Instance<NodeStatus> {

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof ApplicationInstance &&
                Objects.equals(getId(), ((ApplicationInstance) obj).getId())
                ;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String getNodeId() {
        return getId();
    }
}
