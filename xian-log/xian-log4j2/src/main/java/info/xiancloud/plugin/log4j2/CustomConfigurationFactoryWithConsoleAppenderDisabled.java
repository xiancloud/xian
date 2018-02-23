package info.xiancloud.plugin.log4j2;


/**
 * @author happyyangyuan
 * The log4j2 custom configration factory, no log will be written to console.
 */
public class CustomConfigurationFactoryWithConsoleAppenderDisabled extends AbstractCustomConfigurationFactoryTemplate {

    @Override
    protected boolean isConsoleAppenderEnabled() {
        return false;
    }
}