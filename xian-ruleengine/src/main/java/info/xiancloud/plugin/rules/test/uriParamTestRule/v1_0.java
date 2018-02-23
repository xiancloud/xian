package info.xiancloud.plugin.rules.test.uriParamTestRule;

import info.xiancloud.plugin.rules.GroovyRuleController;
import info.xiancloud.plugin.rules.annotation.First;
import info.xiancloud.plugin.rules.annotation.Params;

/**
 * @author happyyangyuan
 */
public class v1_0 extends GroovyRuleController {

    @First
    @Params("$uriParam->myParam")
    public void testService_testUriParam() {
        next = END;
    }
}
