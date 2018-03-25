package info.xiancloud.message.unit;


import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.message.MessageGroup;
import info.xiancloud.message.email.EmailSender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author happyyangyuan
 */
public class SendEmail implements Unit {
    @Override
    public String getName() {
        return "sendEmail";
    }

    @Override
    public Group getGroup() {
        return MessageGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("发送邮件,默认同一个收件人每分钟最多收2封邮件");
    }

    @Override
    public Input getInput() {
        return new Input() {{
            add("recipients", List.class, "收件人", REQUIRED);
            add("subject", String.class, "主题", REQUIRED);
            add("content", String.class, "内容", REQUIRED);
            add("limit", int.class, "时间范围内的限制次数,默认2");
            add("scope", int.class, "距离当前时间范围,毫秒,默认60*1000");
        }};
    }

    private int getLimit(Map map) {
        if (map.get("limit") == null || (int) map.get("limit") == 0) {
            return 2;
        } else {
            return (int) map.get("limit");
        }
    }

    private int getScope(Map map) {
        if (map.get("scope") == null || (int) map.get("scope") == 0) {
            return 60 * 1000;
        } else {
            return (int) map.get("scope");
        }
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        Map map = msg.getArgMap();
        List<String> emailAddresses = new ArrayList<>();
        for (Object mailAddr : (List) map.get("recipients")) {
            String addr = mailAddr.toString();
            if (isLimited(addr, map)) {
                System.out.println(addr + " 已超出最大邮件发送次数...");
                continue;
            }
            records.putIfAbsent(addr, new ArrayList<>());
            records.get(addr).add(System.currentTimeMillis());
            emailAddresses.add(addr);
        }
        try {
            new EmailSender(emailAddresses, map.get("subject").toString(), map.get("content").toString()).send();
        } catch (Throwable e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success("邮件发送请求已发送...");
    }

    private static Map<String, List<Long>> records = new ConcurrentHashMap<>();

    private boolean isLimited(String email, Map map) {
        int count = 0;
        if (!records.containsKey(email)) {
            return false;
        }
        Iterator<Long> it = records.get(email).iterator();
        while (it.hasNext()) {
            Long time = it.next();
            if (time > System.currentTimeMillis() - getScope(map)) {
                //一分钟前
                count++;
            } else {
                it.remove();
            }
        }
        return count > getLimit(map);
    }

    public static void main(String... args) {
        SendEmail.records.put("happyyangyuan@163.com", new ArrayList<Long>() {{
//            add(System.currentTimeMillis());
//            add(System.currentTimeMillis());
//            add(System.currentTimeMillis());
//            add(System.currentTimeMillis());
        }});
        new SendEmail().execute(new UnitRequest(new JSONObject() {{
            put("subject", "XXX账户密码重置");
            put("recipients", new ArrayList<String>() {{
                add("happyyangyuan@163.com");
            }});
            put("content", "您在XXX使用了密码重置功能，请点击下面链接重置密码:\n"
                    + "http://localhost:8080/XXX/ResetPassword?id=jfdsajkfs89878788111"
            );
            put("limit", 3);
            put("scope", 1000 * 30);
        }}));
    }
}
