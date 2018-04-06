package info.xiancloud.core.init.aop;

import info.xiancloud.core.Unit;
import info.xiancloud.core.aop.ISingleUnitAop;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * @author happyyangyuan
 * @deprecated this won't work for asynchronous xian
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
