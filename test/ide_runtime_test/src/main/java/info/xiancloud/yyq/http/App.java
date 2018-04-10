package info.xiancloud.yyq.http;

import info.xiancloud.core.util.http.HttpKit;

public class App {

    public static void main(String[] args) {

        String result = null;
        try {
            result = HttpKit.get("https://localhost/httpsweb/login")
                    .addParam("name", "袁浴钦")
                    .addHeader("token", "123")
                    .setSSL(null, null)
                    //.setContent("yyq")
                    .execute().blockingGet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(result);
    }
}
