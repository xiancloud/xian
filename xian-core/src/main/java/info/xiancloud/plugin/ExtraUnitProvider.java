package info.xiancloud.plugin;

import java.util.List;

/**
 * ExtraUnitProvider interface.
 * Plugins may implement this interface to provide their own units which the framework can not scan.
 *
 * @author happyyangyuan
 */
public interface ExtraUnitProvider {

    /**
     * @return extral units you want to provide.
     */
    List<? extends Unit> provideExtraUnits();

}
