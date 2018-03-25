package info.xiancloud.plugin.yy.stream_rpc;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
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
        return UnitMeta.create().setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("file", String.class, "文件路径", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        File file = new File(msg.getString("file"));
        try {
            InputStream inputStream = new FileInputStream(file);
            return UnitResponse.success(inputStream);
        } catch (FileNotFoundException e) {
            return UnitResponse.exception(e, "文件不存在");
        }
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

}
