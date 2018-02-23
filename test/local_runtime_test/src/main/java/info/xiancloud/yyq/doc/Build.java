package info.xiancloud.yyq.doc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import info.xiancloud.apidoc.APIBuild;
import info.xiancloud.apidoc.handler.BuildCallback;
import info.xiancloud.apidoc.handler.UnitBuildHandler;
import info.xiancloud.apidoc.handler.OAuth20BuildHandler;
import info.xiancloud.plugin.util.LOG;

public class Build {

    public static void main(String[] args) {

        // "e:/unit.md",e:/oauth20.md
        APIBuild.build(new UnitBuildHandler().callback(data -> {
            try {
                OutputStream os = new FileOutputStream("e:/unit.md");
                os.write(data);
                os.flush();
                os.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }), new OAuth20BuildHandler().callback(data -> {
            try {
                OutputStream os = new FileOutputStream("e:/oauth20.md");
                os.write(data);
                os.flush();
                os.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }));
    }
}
