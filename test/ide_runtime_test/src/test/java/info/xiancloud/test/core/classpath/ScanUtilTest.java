package info.xiancloud.test.core.classpath;

import info.xiancloud.core.Group;
import info.xiancloud.core.Unit;
import info.xiancloud.core.util.ClassGraphUtil;
import org.junit.Test;

/**
 * test classpath scan util
 */
public class ScanUtilTest {

    private static final String PACKAGE = "info.xiancloud";

    @Test
    public void scanUnits() {
        // make sure all xian internal units are defined in "info.xiancloud" package.
        for (Class<? extends Unit> unitClass : ClassGraphUtil.getNonAbstractSubClasses(Unit.class, "")) {
            if (!unitClass.getPackage().getName().startsWith(PACKAGE)) {
                System.out.println(unitClass.getName());
                throw new RuntimeException("All xian internal units must be defined in package: " + PACKAGE);
            }
        }

        // make sure all xian internal groups are defined in "info.xiancloud" package.
        for (Class<? extends Group> groupClass : ClassGraphUtil.getNonAbstractSubClasses(Group.class, "")) {
            if (!groupClass.getPackage().getName().startsWith(PACKAGE)) {
                System.out.println(groupClass.getName());
                throw new RuntimeException("All xian internal groups must be defined in package: " + PACKAGE);
            }
        }
    }


}
