package info.xiancloud.apidoc;

import info.xiancloud.plugin.Group;

public class APIBuildServiceGroup implements Group {

    public static final Group singleton = new APIBuildServiceGroup();

    @Override
    public String getName() {
        return "apiBuildService";
    }

    @Override
    public String getDescription() {
        return "Api doc building service group.";
    }

}
