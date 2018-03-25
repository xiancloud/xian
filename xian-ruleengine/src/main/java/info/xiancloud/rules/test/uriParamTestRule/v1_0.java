package info.xiancloud.rules.test.uriParamTestRule;

import info.xiancloud.rules.GroovyRuleController;
import info.xiancloud.rules.annotation.First;
import info.xiancloud.rules.annotation.Params;

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
