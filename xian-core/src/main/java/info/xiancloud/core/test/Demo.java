package info.xiancloud.core.test;


import info.xiancloud.core.message.SingleRxXian;

import java.util.HashMap;
import java.util.Map;


public class Demo {

    public static void main(String[] args) {

        Map<String, Object> map = new HashMap<>();
        map.put("userName", "xianUser");
        map.put("password", "xianPwd");
        SingleRxXian.call("test", "UnitResponseTestUnit", map)
                .subscribe(
                        response -> System.out.println(response.toVoJSONString())
                );
    }

}
