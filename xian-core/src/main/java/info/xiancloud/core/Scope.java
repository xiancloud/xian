package info.xiancloud.core;

import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.core.distribution.exception.UnitUndefinedException;
import info.xiancloud.core.distribution.loadbalance.UnitRouter;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;

/**
 * A scope is a label for units.
 *
 * @author happyyangyuan
 */
public class Scope {
    /**
     * requests from white ip list, have right to access to all api.
     * We give 'api_all' scope to this request.
     */
    public static final String api_all = "api_all";


    public static boolean validate(String scope, String groupName, String unitName) {
        if (StringUtil.isEmpty(scope))
            return false;
        if (api_all.equals(scope)) {
            LOG.debug("具有api_all的scope直接放通");
            return true;
        } else {
            try {
                Unit unit = UnitRouter.singleton.newestDefinition(Unit.fullName(groupName, unitName));
                return unit.getMeta().getScopes().contains(scope);
            } catch (UnitUndefinedException e) {
                LOG.warn(String.format("%s.%s验证scope不通过", groupName, unitName), e);
                return false;
            }
        }
    }
}
