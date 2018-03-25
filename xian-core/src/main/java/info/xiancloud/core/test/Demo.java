package info.xiancloud.core.test;


import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;
import info.xiancloud.core.NotifyHandler;

import java.util.HashMap;
import java.util.Map;


public class Demo {

    public static void main(String[] args) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userName", "xianUser");
        map.put("password", "xianPwd");
        Xian.call("test", "UnitResponseTestUnit", map, new NotifyHandler() {
            @Override
            protected void handle(UnitResponse unitResponse) {
                System.out.println(unitResponse.toVoJSONString());
            }
        });
    }

}
