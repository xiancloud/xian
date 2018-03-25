package info.xiancloud.yy.log;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.LOG;

/**
 * 测试LOG打印error json，并且传递一个异常对象的场景
 *
 * @author happyyangyuan
 */
public class TestLogErrorJSON {
    public static void main(String[] args) {
        LOG.error(new JSONObject() {{
            put("type", "ff");
            put("gg", "ff");
        }}, new Throwable());
        System.out.println("得出结论：既支持json也支持打印堆栈");
    }

}
