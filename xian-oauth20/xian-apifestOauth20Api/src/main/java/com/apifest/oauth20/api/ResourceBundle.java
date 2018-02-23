package com.apifest.oauth20.api;

public abstract class ResourceBundle {

    protected static ResourceBundle instance;

    public abstract String getProperty(String key);

    public abstract String getProperty(String key, String defaultValue);

    public static ResourceBundle instance() {
        if (instance == null) {
            throw new IllegalStateException("Resource bundle has not been initialized");
        }
        return instance;
    }

}
