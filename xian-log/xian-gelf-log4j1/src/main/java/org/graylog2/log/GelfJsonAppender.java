package org.graylog2.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.graylog2.GelfMessage;
import org.graylog2.GelfMessageFactory;
import org.graylog2.GelfSenderResult;

/**
 * A GelfAppender which will parse the given JSON message into additional fields in GELF
 *
 * @author Anton Yakimov
 * @author Jochen Schalanda
 * @author the-james-burton
 * @author happyyangyuan
 */
public class GelfJsonAppender extends GelfAppender {

    @Override
    protected void append(final LoggingEvent event) {
        GelfMessage gelfMessage = GelfMessageFactory.makeMessage(layout, event, this);
        String originalMessage;
        if (event != null && event.getMessage() != null) {
            originalMessage = event.getMessage().toString().trim();
            if (originalMessage.startsWith("{")/* && originalMessage.endsWith("}")*/) {
                try {
                    JSONObject fields = (JSONObject) JSON.parse(originalMessage);
                    for (String key : fields.keySet()) {
                        gelfMessage.getAdditonalFields().put(key, fields.get(key));
                    }
                } catch (JSONException ignored) {
                    //ignored because the log content is not a json string.
                }
            }
        }
        if (getGelfSender() == null) {
            errorHandler.error("Could not send GELF message. Gelf Sender is not initialised and equals null");
        } else {
            GelfSenderResult gelfSenderResult = getGelfSender().sendMessage(gelfMessage);
            if (!GelfSenderResult.OK.equals(gelfSenderResult)) {
                errorHandler.error("Error during sending GELF message. Error code: " + gelfSenderResult.getCode() + ".",
                        gelfSenderResult.getException(), ErrorCode.WRITE_FAILURE);
            }
        }
    }

}
