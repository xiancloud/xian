package biz.paluch.logging.gelf.log4j2;

import biz.paluch.logging.gelf.GelfMessageAssembler;
import biz.paluch.logging.gelf.MdcGelfMessageAssembler;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;

/**
 * <p>builder for gelf-log4j2 appender</p>
 *
 * @author happyyangyuan
 * @see biz.paluch.logging.gelf.MdcGelfJsonMessageAssembler
 * @deprecated Sorry for that I copied this code from gelf4j which is an open source gelf logging client for log4j 1.x . And found out that
 * this appender lever builder is not suitable for this logging framework.
 * After a period of investigation, I found that {@link GelfMessageAssembler} level builder is needed instead of appender level builder.
 */
public class GelfAppenderBuilder {

    private boolean withJsonAppender;
    private String name;
    private Filter filter;
    private Layout layout;
    private MdcGelfMessageAssembler mdcGelfMessageAssembler;
    private boolean ignoreExceptions;

    public static GelfAppenderBuilder newBuilder() {
        return new GelfAppenderBuilder();
    }

    /**
     * @param withJsonAppender json or not json.
     */
    public GelfAppenderBuilder withJsonAppender(boolean withJsonAppender) {
        this.withJsonAppender = withJsonAppender;
        return this;
    }

    public GelfAppenderBuilder name(String name) {
        this.name = name;
        return this;
    }

    public GelfAppenderBuilder filter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public GelfAppenderBuilder layout(Layout layout) {
        this.layout = layout;
        return this;
    }

    public GelfAppenderBuilder mdcGelfMessageAssembler(MdcGelfMessageAssembler mdcGelfMessageAssembler) {
        this.mdcGelfMessageAssembler = mdcGelfMessageAssembler;
        return this;
    }

    public GelfAppenderBuilder ignoreExceptions(boolean ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
        return this;
    }

    public Appender build() {
        if (withJsonAppender) {
            //In fact we are using MdcGelfJsonMessageAssembler to parse the json log message. And this GelfJsonLogAppender
            //does nothing more than appending the log to gelf stream.
            return new GelfJsonLogAppender(name, filter, layout, mdcGelfMessageAssembler, ignoreExceptions);
        }
        return new GelfLogAppender(name, filter, layout, mdcGelfMessageAssembler, ignoreExceptions);
    }

}
