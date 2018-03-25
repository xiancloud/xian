package info.xiancloud.log4j2.test;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.LOG;
import org.junit.Test;

/**
 * @author happyyangyuan
 */
public class IdeLog4j2Test {

    @Test
    public void info() {
        LOG.info("yy");
        LOG.info(new JSONObject() {{
            put("yy", "xx");
        }});
    }
}
