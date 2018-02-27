package info.xiancloud.plugin.debug.unit;

import com.alibaba.fastjson.JSON;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.TraverseClasspath;

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
        return UnitMeta.create().setBroadcast(UnitMeta.Broadcast.create().setSuccessDataOnly(true));
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
    public UnitResponse execute(UnitRequest msg) {
        try {
            Method defaultPackages = TraverseClasspath.class.getDeclaredMethod("defaultPackages");
            defaultPackages.setAccessible(true);
            return UnitResponse.success(JSON.toJSONString(defaultPackages.invoke(null)));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
