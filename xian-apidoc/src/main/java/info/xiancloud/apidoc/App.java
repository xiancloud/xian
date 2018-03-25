package info.xiancloud.apidoc;

import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;

import java.util.HashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("subDec", " * some third access \r\n\r\n * appId=e8249b116f5efa7c67ab1e7c79d8ba02cbe2451a，appSecret=另行邮件通知 \r\n\r\n  除获取token接口外，其他接口必须传入header：xian-accessToken=<yourToken>\r\n");
        params.put("docName", "接口文档");
        //params.put("path", "test");
        params.put("unitFilter", "apiBuildService.apiDocUnit");//"[{'apiBuildService':['apiDocUnit']}]"
        Xian.call("apiBuildService", "apiDocUnit", params, new NotifyHandler() {
            @Override
            protected void handle(UnitResponse unitResponse) {
                System.out.println(unitResponse.toString());
            }
        });
    }
}
