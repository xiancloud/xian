package info.xiancloud.apidoc;

import info.xiancloud.apidoc.handler.MdBuilderHandler;

/**
 * @author yyq, happyyangyuan
 */
public class ApiBuilder {

    public static void build(MdBuilderHandler... mdBuilderHandlers) {

        if (mdBuilderHandlers != null && mdBuilderHandlers.length > 0) {
            for (MdBuilderHandler mdBuilderHandler : mdBuilderHandlers) {
                mdBuilderHandler.build();
            }
        }
    }

}
