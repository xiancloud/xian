package info.xiancloud.core.distribution.unit;

import info.xiancloud.core.*;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * get definition of the specified unit.
 *
 * @author happyyangyuan
 */
public class GetUnitDefinition implements Unit {

    @Override
    public Group getGroup() {
        return SystemGroup.singleton;
    }

    @Override
    public String getName() {
        return "getUnitDefinition";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("获取unit的完整定义").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("group", String.class, "group名", REQUIRED)
                .add("unit", String.class, "unit名", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String fullName = Unit.fullName(msg.getString("group"), msg.getString("unit"));
        try {
            handler.handle(UnitResponse.createSuccess(UnitRouter.singleton.newestDefinition(fullName)));
        } catch (UnitUndefinedException e) {
            handler.handle(UnitResponse.createError(e.getCode(), fullName, e.getLocalizedMessage()));
        }
    }
}
