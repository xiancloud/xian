package info.xiancloud.core.aop;

import info.xiancloud.core.Unit;
import info.xiancloud.core.LocalUnitsManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author happyyangyuan
 * @deprecated this won't work for asynchronous xian
 */
public interface ISingleUnitAop extends IUnitAop {

    String getService();

    String getUnit();

    @Override
    default Collection<Unit> getUnitCollection() {
        return new HashSet<Unit>() {{
            add(LocalUnitsManager.getLocalUnit(getService(), getUnit()));
        }};
    }
}
