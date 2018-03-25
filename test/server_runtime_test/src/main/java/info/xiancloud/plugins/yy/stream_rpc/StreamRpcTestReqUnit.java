package info.xiancloud.plugins.yy.stream_rpc;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.file.FileUtil;

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
