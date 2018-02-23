package info.xiancloud.plugin.init.aop;

import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.aop.ISingleUnitAop;
import info.xiancloud.plugin.message.UnitRequest;

/**
 * @author happyyangyuan
 */
public class TestUnitAop implements ISingleUnitAop {
    @Override
    public String getService() {
        return null;
    }

    @Override
    public String getUnit() {
        return null;
    }

    @Override
    public Object before(Unit unit, UnitRequest unitRequest/*,AOPSession ssn*/) throws UnitResponseReplacement {
        return null;
    }

    @Override
    public void after(Unit unit, UnitRequest unitRequest, UnitResponse unitResponse, Object beforeReturn/*,AOPSession ssn*/) throws UnitResponseReplacement {

    }
}
