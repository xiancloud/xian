package info.xiancloud.core.log;

import info.xiancloud.core.init.Initable;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.init.Initable;
import info.xiancloud.core.util.Reflection;

import java.util.List;

/**
 * The interface for plugins to implement the centralized log initializer.
 * If more than one implementations found in the classpath, the first one is used.
 *
 * @author happyyangyuan
 */
public interface ICentralizedLogInitializer extends Initable {
    List<ICentralizedLogInitializer> centralizedLoggerInitializers = Reflection.getSubClassInstances(ICentralizedLogInitializer.class);
    ICentralizedLogInitializer singleton = centralizedLoggerInitializers.isEmpty() ? null : centralizedLoggerInitializers.get(0);
}
