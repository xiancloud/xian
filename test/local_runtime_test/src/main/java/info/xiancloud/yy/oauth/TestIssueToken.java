package info.xiancloud.yy.oauth;

import com.alibaba.fastjson.JSONObject;
import com.apifest.oauth20.unit.IssueAccessToken;
import info.xiancloud.cache.startup.RedisStartup;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * @author happyyangyuan
 */
public class TestIssueToken {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName(RedisStartup.class.getName());
        JSONObject body = new JSONObject().fluentPut("appId", "25874717e9b1807c5cfb66427f7d3c8da02ecfe4")
                .fluentPut("appSecret", "fa090344347a2cb867833678754e461c36449dc2429e9ee228ddaf3bd1ac0330");
        UnitResponse o = new IssueAccessToken().execute(new UnitRequest().setArgMap(body));
        System.out.println(o);
    }
}
