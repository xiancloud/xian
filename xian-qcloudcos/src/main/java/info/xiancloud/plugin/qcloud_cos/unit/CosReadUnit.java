package info.xiancloud.plugin.qcloud_cos.unit;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.qcloud_cos.sdk.CosFileReader;

/**
 * @author happyyangyuan
 */
public class CosReadUnit implements Unit {
    @Override
    public String getName() {
        return "cosRead";
    }

    @Override
    public Group getGroup() {
        return CosGroup.singleton;
    }


    @Override
    public Input getInput() {
        return new Input().add("path", String.class, "业务相对路径", REQUIRED)
                ;

    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        CosFileReader reader = new CosFileReader();
        try {
            String content = reader.forPath(msg.get("path", String.class));
            return UnitResponse.success(content);
        } catch (Throwable e) {
            return UnitResponse.exception(e);
        } finally {
            reader.close();
        }
    }
}
