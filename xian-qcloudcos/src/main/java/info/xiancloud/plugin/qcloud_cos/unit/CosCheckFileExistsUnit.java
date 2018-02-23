package info.xiancloud.plugin.qcloud_cos.unit;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.qcloud_cos.sdk.CosFileReader;

/**
 * @author happyyangyuan
 */
public class CosCheckFileExistsUnit implements Unit {
    @Override
    public String getName() {
        return "cosCheckFileExists";
    }

    @Override
    public Group getGroup() {
        return new CosGroup();
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("path", String.class, "业务相对路径", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        boolean exists = new CosFileReader().exists(msg.get("path", String.class));
        return UnitResponse.success(exists);
    }

}
