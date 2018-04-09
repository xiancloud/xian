package info.xiancloud.plugin.yy.stream_rpc;

import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author happyyangyuan
 */
public class StreamRpcTestResUnit implements Unit {

    @Override
    public String getName() {
        return "streamRpcTestRes";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDocApi(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("file", String.class, "文件路径", REQUIRED);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        File file = new File(msg.getString("file"));
        try {
            InputStream inputStream = new FileInputStream(file);
            handler.handle(UnitResponse.createSuccess(inputStream));
            return;
        } catch (FileNotFoundException e) {
            handler.handle(UnitResponse.createException(e, "文件不存在"));
            return;
        }
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

}
