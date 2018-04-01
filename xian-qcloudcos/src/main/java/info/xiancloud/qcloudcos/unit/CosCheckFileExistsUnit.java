package info.xiancloud.qcloudcos.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.qcloudcos.sdk.CosFileReader;

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
        return UnitResponse.createSuccess(exists);
    }

}
