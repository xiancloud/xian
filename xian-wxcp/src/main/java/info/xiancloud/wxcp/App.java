package info.xiancloud.wxcp;

import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.message.UnitResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试类
 *
 * @author yyq
 */
public class App {

    public static void main(String[] args) {

        Map<String, Object> params = new HashMap<>();
        params.put("content", "消息接口测试");

        UnitResponse result = SyncXian.call("wxcp", "wxcpMessage", params);
        System.out.println(result.toJSONString());
    }
}
