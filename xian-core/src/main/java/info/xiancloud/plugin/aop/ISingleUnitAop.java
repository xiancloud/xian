package info.xiancloud.plugin.aop;

import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.LocalUnitsManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author happyyangyuan
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
