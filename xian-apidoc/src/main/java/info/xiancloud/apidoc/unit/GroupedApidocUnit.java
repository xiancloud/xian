package info.xiancloud.apidoc.unit;

import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.distribution.GroupBean;
import info.xiancloud.plugin.distribution.exception.GroupOfflineException;
import info.xiancloud.plugin.distribution.exception.GroupUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.GroupRouter;
import info.xiancloud.plugin.distribution.service_discovery.GroupInstance;
import info.xiancloud.plugin.message.UnitRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * apidoc builder for a specified group
 *
 * @author happyyangyuan
 */
public class GroupedApidocUnit extends AbstractApidocUnit {
    @Override
    public Input otherInput() {
        return Input.create().add("groupName", String.class, "group name", REQUIRED);
    }

    @Override
    protected Map<String, List<String>> filter(UnitRequest request) {
        Map<String, List<String>> filter = new HashMap<>();
        String groupName = request.getString("groupName");
        try {
            GroupInstance groupInstance = GroupRouter.singleton.firstInstance(groupName);
            GroupBean groupBean = groupInstance.getPayload();
            filter.put(groupName, groupBean.getUnitNames());
            return filter;
        } catch (GroupOfflineException | GroupUndefinedException e) {
            throw new RuntimeException(e);
        }
    }

}
