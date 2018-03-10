package info.xiancloud.apidoc;

import info.xiancloud.plugin.Group;

/**
 * @author yyq, happyyangyuan
 */
public class ApidocGroup implements Group {

    public static final Group singleton = new ApidocGroup();

    @Override
    public String getName() {
        return "apidoc";
    }

    @Override
    public String getDescription() {
        return "Api doc building service group.";
    }

}
