package info.xiancloud.plugin.test.without_service_test;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Unit;

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
