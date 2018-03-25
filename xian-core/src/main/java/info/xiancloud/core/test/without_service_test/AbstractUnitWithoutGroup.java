package info.xiancloud.core.test.without_service_test;

import info.xiancloud.core.Group;
import info.xiancloud.core.Unit;

/**
 * This class exists for validating the unit scanner.
 *
 * @author happyyangyuan
 */
abstract class AbstractUnitWithoutGroup implements Unit {
    @Override
    public Group getGroup() {
        return null;
    }
}
