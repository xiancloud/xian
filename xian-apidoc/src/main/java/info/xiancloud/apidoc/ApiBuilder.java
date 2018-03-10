package info.xiancloud.apidoc;

import info.xiancloud.apidoc.handler.BuildHandler;

/**
 * @author yyq, happyyangyuan
 */
public class ApiBuilder {

    public static void build(BuildHandler... buildHandlers) {

        if (buildHandlers != null && buildHandlers.length > 0) {
            for (BuildHandler buildHandler : buildHandlers) {
                buildHandler.build();
            }
        }
    }

}
