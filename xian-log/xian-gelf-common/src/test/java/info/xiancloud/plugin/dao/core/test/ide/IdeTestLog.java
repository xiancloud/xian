package info.xiancloud.plugin.dao.core.test.ide;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.log.ICentralizedLogInitializer;
import info.xiancloud.plugin.util.LOG;
import org.junit.Test;

/**
 * @author happyyangyuan
 */
public class IdeTestLog {

    @Test
    public void info() {
        ICentralizedLogInitializer.singleton.init();
        LOG.info("yy");
        LOG.info(new JSONObject() {{
            put("yy", "xx");
        }});
    }
}
