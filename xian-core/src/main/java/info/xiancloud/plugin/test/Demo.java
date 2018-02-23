package info.xiancloud.plugin.test;


import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;

import java.util.HashMap;
import java.util.Map;


public class Demo {

    public static void main(String[] args) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userName", "xianUser");
        map.put("password", "xianPwd");
        Xian.call("test", "UnitResponseTestUnit", map, new NotifyHandler() {
            @Override
            protected void toContinue(UnitResponse unitResponse) {
                System.out.println(unitResponse.toVoJSONString());
            }
        });
    }

}
