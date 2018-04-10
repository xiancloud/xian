package info.xiancloud.core.debug.unit;

import com.alibaba.fastjson.JSON;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.TraverseClasspath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * get the packages to scan
 *
 * @author happyyangyuan
 */
public class GetPackagesToScan implements Unit {

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setBroadcast(UnitMeta.Broadcast.create().setSuccessDataOnly(true))
                .setDocApi(false);
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Group getGroup() {
        return SystemGroup.singleton;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        try {
            Method defaultPackages = TraverseClasspath.class.getDeclaredMethod("defaultPackages");
            defaultPackages.setAccessible(true);
            handler.handle(UnitResponse.createSuccess(JSON.toJSONString(defaultPackages.invoke(null))));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
