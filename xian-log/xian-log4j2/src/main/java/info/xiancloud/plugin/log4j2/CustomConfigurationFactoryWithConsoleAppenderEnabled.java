package info.xiancloud.plugin.log4j2;


/**
 * CustomConfigurationFactoryWithConsoleAppenderEnabled
 *
 * @author happyyangyuan
 */
public class CustomConfigurationFactoryWithConsoleAppenderEnabled extends AbstractCustomConfigurationFactoryTemplate {
    @Override
    protected boolean isConsoleAppenderEnabled() {
        return true;
    }
}
