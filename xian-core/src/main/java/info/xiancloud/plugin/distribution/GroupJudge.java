package info.xiancloud.plugin.distribution;

import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.distribution.exception.GroupUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.GroupRouter;
import info.xiancloud.plugin.distribution.service_discovery.GroupDiscovery;

/**
 * Judge group
 *
 * @author happyyangyuan
 */
public class GroupJudge {

    /**
     * Judge whether the specified group is defined.
     * We check local first and then remote.
     */
    public static boolean defined(String group) {
        return LocalUnitsManager.getGroupByName(group) != null ||
                (GroupDiscovery.singleton != null && GroupDiscovery.singleton.newestDefinition(group) != null);
    }

    /**
     * check whether the specified unit is available.
     * tips: don't use exception catching to get the result, cause this is bad performance.
     * We check local first and then remote.
     */
    public static boolean available(String groupName) {
        return LocalUnitsManager.getGroupByName(groupName) != null ||
                (GroupDiscovery.singleton != null && GroupDiscovery.singleton.firstInstance(groupName) != null);
    }

    /**
     * Judge whether the specified group is a dao group.
     *
     * @param groupName the specified group name.
     * @return true if this group's dao property value is true, other wise flase.
     */
    public static boolean isDao(String groupName) {
        try {
            return GroupRouter.singleton.newestDefinition(groupName).isDao();
        } catch (GroupUndefinedException e) {
            throw new RuntimeException("Call GroupJudge.defined() first.", e);
        }
    }
}
