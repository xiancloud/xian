package biz.paluch.logging.gelf;

import biz.paluch.logging.gelf.intern.GelfMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * @author happyyangyuan
 */
public class MdcGelfJsonMessageAssembler extends MdcGelfMessageAssembler {
    @Override
    public GelfMessage createGelfMessage(LogEvent event) {//todo factory is better than overwriting.
        GelfMessage gelfMessage = super.createGelfMessage(event);
        if (event != null && event.getMessage() != null) {
            String originalMessage = event.getMessage().trim();
            if (originalMessage.startsWith("{")/* && originalMessage.endsWith("}")*/) {
                try {
                    JSONObject fields = (JSONObject) JSON.parse(originalMessage);
                    for (String key : fields.keySet()) {
                        gelfMessage.addField(key, fields.get(key) + "");
                    }
                } catch (JSONException ignored) {
                    //ignored because the log content is not a json string.
                }
            }
        }
        return gelfMessage;
    }
}
