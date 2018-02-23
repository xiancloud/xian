package info.xiancloud.plugins.yy.stream_rpc;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.SyncXian;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.test.TestGroup;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.file.FileUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author happyyangyuan
 */
public class StreamRpcTestReqUnit implements Unit {

    @Override
    public Input getInput() {
        return new Input()
                .add("file", String.class, "")
                .add("newFile", String.class, "");
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        UnitResponse o = SyncXian.call(TestGroup.singleton.getName(), "streamRpcTestRes", new JSONObject() {{
            put("file", msg.getString("file", "/Users/happyyangyuan/Downloads/zz.txt"));
        }});
        try {
            FileUtil.copyFile(o.dataToType(InputStream.class), msg.getString("newFile", "/Users/happyyangyuan/Downloads/yy.txt"));
            return UnitResponse.success();
        } catch (IOException e) {
            return UnitResponse.exception(e);
        } finally {
            try {
                o.dataToType(InputStream.class).close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

}
