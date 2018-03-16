package info.xiancloud.plugin.distribution.service_discovery;

import info.xiancloud.plugin.distribution.GroupProxy;

/**
 * @author happyyangyuan
 */
public class GroupInstance extends Instance<GroupProxy> {

    private GroupInstanceIdBean groupInstanceIdBean;

    @Override
    public String getNodeId() {
        return groupInstanceIdBean.getNodeId();
    }

    @Override
    public void setId(String id) {
        super.setId(id);
        this.groupInstanceIdBean = new GroupInstanceIdBean(id);
    }

    public void setGroupInstanceIdBean(GroupInstanceIdBean groupInstanceIdBean) {
        super.setId(groupInstanceIdBean.getGroupInstanceId());
        this.groupInstanceIdBean = groupInstanceIdBean;
    }
}
