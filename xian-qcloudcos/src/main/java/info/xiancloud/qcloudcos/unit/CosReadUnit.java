package info.xiancloud.qcloudcos.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.qcloudcos.sdk.CosFileReader;

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
            return UnitResponse.createSuccess(content);
        } catch (Throwable e) {
            return UnitResponse.createException(e);
        } finally {
            reader.close();
        }
    }
}
