package info.xiancloud.plugins.yy.stream_rpc;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.*;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.test.TestGroup;
import info.xiancloud.core.util.file.FileUtil;

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
    public UnitMeta getMeta() {
        return UnitMeta.create().setDocApi(false);
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        SingleRxXian
                .call(TestGroup.singleton.getName(), "streamRpcTestRes", new JSONObject() {{
                    put("file", msg.getString("file", "/Users/happyyangyuan/Downloads/zz.txt"));
                }})
                .subscribe(o -> {
                    try {
                        FileUtil.copyFile(o.dataToType(InputStream.class), msg.getString("newFile", "/Users/happyyangyuan/Downloads/yy.txt"));
                        handler.handle(UnitResponse.createSuccess());
                    } finally {
                        o.dataToType(InputStream.class).close();
                    }
                });
    }

    @Override
    public Group getGroup() {
        return TestGroup.singleton;
    }

}
