package info.xiancloud.plugin.distribution.unit;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.distribution.exception.UnitOfflineException;
import info.xiancloud.plugin.distribution.exception.UnitUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.UnitRouter;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;

/**
 * find out which node the group/unit is in
 *
 * @author happyyangyuan
 */
public class GetUnitNode implements Unit {
    @Override
    public Group getGroup() {
        return SystemGroup.singleton;
    }

    @Override
    public String getName() {
        return "getUnitNode";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("find out which node the group/unit is in")
                .setPublic(false)
                ;
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("group", String.class, "group name", REQUIRED)
                .add("unit", String.class, "unit name", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String group = msg.getString("group");
        String unit = msg.getString("unit");
        try {
            return UnitResponse.success(UnitRouter.singleton.allInstances(Unit.fullName(group, unit)));
        } catch (UnitOfflineException | UnitUndefinedException e) {
            return UnitResponse.failure(null, e.getLocalizedMessage());
        }
    }
}
