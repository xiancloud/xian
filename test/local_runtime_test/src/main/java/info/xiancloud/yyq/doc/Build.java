package info.xiancloud.yyq.doc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import info.xiancloud.apidoc.ApiBuilder;
import info.xiancloud.apidoc.handler.UnitMdBuilderHandler;
import info.xiancloud.apidoc.handler.OAuth20MdBuilderHandler;
import info.xiancloud.plugin.util.LOG;

public class Build {

    public static void main(String[] args) {

        // "e:/unit.md",e:/oauth20.md
        ApiBuilder.build(new UnitMdBuilderHandler().callback(data -> {
            try {
                OutputStream os = new FileOutputStream("e:/unit.md");
                os.write(data);
                os.flush();
                os.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }), new OAuth20MdBuilderHandler().callback(data -> {
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
