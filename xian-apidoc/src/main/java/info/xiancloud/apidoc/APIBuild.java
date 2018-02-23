package info.xiancloud.apidoc;

import info.xiancloud.apidoc.handler.BuildHandler;

public class APIBuild {

	public static void build(BuildHandler... buildHandlers) {

		if (buildHandlers != null && buildHandlers.length > 0) {
           for(BuildHandler buildHandler:buildHandlers){
        	   buildHandler.build();
           }
		}
	}

}
