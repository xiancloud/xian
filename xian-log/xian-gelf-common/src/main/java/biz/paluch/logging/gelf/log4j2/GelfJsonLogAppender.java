package biz.paluch.logging.gelf.log4j2;

import biz.paluch.logging.gelf.MdcGelfJsonMessageAssembler;
import biz.paluch.logging.gelf.MdcGelfMessageAssembler;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.plugins.Plugin;

/**
 * <p>Spread the json message into additional fields.</p>
 *
 * @author happyyangyuan
 * @deprecated We use {@link MdcGelfJsonMessageAssembler} instead of this jsonAppender.
 */
@Plugin(name = "GelfJson", category = "Core", elementType = "appender", printObject = true)
public class GelfJsonLogAppender extends GelfLogAppender {
    public GelfJsonLogAppender(String name, Filter filter, Layout layout, MdcGelfMessageAssembler gelfMessageAssembler, boolean ignoreExceptions) {
        super(name, filter, layout, gelfMessageAssembler, ignoreExceptions);
    }
}
