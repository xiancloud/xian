package info.xiancloud.core.distribution;

import com.alibaba.fastjson.JSON;
import info.xiancloud.core.Input;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;

/**
 * unit proxy bean, for performance consideration this class extends {@link UnitBean} which cached the {@link UnitMeta} and the {@link Input}
 *
 * @author happyyangyuan
 */
public class UnitProxy extends UnitBean {
    private Unit unit;

    public static UnitProxy create(Unit unit) {
        /*JSONObject unitJsonObject = (JSONObject) JSON.toJSON(unit);
        UnitProxy proxy = unitJsonObject.toJavaObject(UnitProxy.class);
        以上方式会触发非unit定义的getter被调用，存在潜在风险*/
        UnitProxy proxy = JSON.parseObject(unit.toJSONString(), UnitProxy.class);
        proxy.unit = unit;
        return proxy;
    }

    @Override
    public void execute(UnitRequest request, NotifyHandler handler) {
        unit.execute(request, handler);
    }

}
