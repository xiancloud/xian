package com.apifest.oauth20;

import java.util.Properties;

import com.apifest.oauth20.api.ResourceBundle;

/**
 * Class that encapsulates properties for the custom classes jar
 *
 *  They are loaded from a properties file with the same name as the jar in the same folder.
 *  For example if the path to the custom classes jar is /home/apifest-oauth/custom-classes.jar
 *  then the path the properties file would be /home/apifest-oauth/custom-classes.jar.properties.
 *
 *  This properties file is optional - if it is not present the Properties accessed through this class
 *  will be empty
 *
 */
public class ResourceBundleImpl extends ResourceBundle {

    private Properties properties = null;

    public ResourceBundleImpl(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void install() {
        ResourceBundle.instance = this;
    }
}
