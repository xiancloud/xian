package info.xiancloud.dao.jdbc.sql;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.ProxyBuilder;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

/**
 * @author happyyangyuan
 */
public interface Action extends ISqlLogger {

    UnitResponse execute(Unit daoUnit, Map map, Connection connection);

    long SLOW_QUERY_IN_MILLIS = 5 * 1000;

    static Action createActionProxy(Action action) {
        return new ProxyBuilder<Action>(action, false) {
            public Object before(Method method, Object[] args) {
                if (method.getName().equals("execute")) {
                    return System.nanoTime();
                }
                return null;
            }

            public void after(Method method, Object[] args, Object methodReturn, Object beforeReturn) {
                if (method.getName().equals("execute")) {
                    String sql = ((AbstractAction) getOriginalTarget()).sqlPattern;
                    Long howLong = ((System.nanoTime() - (long) beforeReturn)) / 1000000;
                    JSONObject sqlLog = new JSONObject() {{
                        put("type", "sql");
                        put("cost", howLong);
                        put("sql", sql);
                        put("description", "执行SQL耗时 ".concat(howLong.toString()).concat(" ms"));
                    }};
                    if (howLong > SLOW_QUERY_IN_MILLIS) {
                        sqlLog.put("description", String.format("超过%sms的慢查询", SLOW_QUERY_IN_MILLIS));
                        LOG.error(sqlLog.toJSONString());
                    } else {
                        LOG.info(sqlLog.toJSONString());
                    }
                }
            }
        }.getProxy();
    }

    static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            long start = System.nanoTime();
            Thread.sleep(1000);
            System.out.println((System.nanoTime() - start) / 1000000);


            long start1 = System.currentTimeMillis();
            Thread.sleep(1000);
            System.out.println(System.currentTimeMillis() - start1);

            System.out.println("---------------------------");
        }

    }

}
