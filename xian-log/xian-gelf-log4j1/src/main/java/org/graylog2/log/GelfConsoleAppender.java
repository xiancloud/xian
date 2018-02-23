package org.graylog2.log;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.graylog2.GelfMessage;
import org.graylog2.GelfMessageFactory;
import org.graylog2.GelfMessageProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GelfConsoleAppender extends ConsoleAppender implements GelfMessageProvider{

    private static String originHost;
    private boolean extractStacktrace;
    private boolean addExtendedInformation;
    private boolean includeLocation = true;
    private Map<String, String> fields;

    // parent overrides.

    public GelfConsoleAppender() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public GelfConsoleAppender(Layout layout) {
        super(layout);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public GelfConsoleAppender(Layout layout, String target) {
        super(layout, target);    //To change body of overridden methods use File | Settings | File Templates.
    }

    // GelfMessageProvider interface.

    public void setAdditionalFields(String additionalFields) {
        fields = (Map<String, String>) JSON.parse(additionalFields.replaceAll("'", "\""));
    }

    public boolean isExtractStacktrace() {
        return extractStacktrace;
    }

    public void setExtractStacktrace(boolean extractStacktrace) {
        this.extractStacktrace = extractStacktrace;
    }

    public boolean isAddExtendedInformation() {
        return addExtendedInformation;
    }

    public void setAddExtendedInformation(boolean addExtendedInformation) {
        this.addExtendedInformation = addExtendedInformation;
    }

    public boolean isIncludeLocation() {
        return this.includeLocation;
    }

    public void setIncludeLocation(boolean includeLocation) {
        this.includeLocation = includeLocation;
    }

    public String getOriginHost() {
        return originHost;
    }

    public void setOriginHost(String originHost) {
        this.originHost = originHost;
    }

    public String getFacility() {
        return null;
    }

    public Map<String, String> getFields() {
        if (fields == null) {
            fields = new HashMap<String, String>();
        }
        return Collections.unmodifiableMap(fields);
    }
    
    public Object transformExtendedField(String field, Object object) {
        if (object != null)
            return object.toString();
        return null;
    }

    // the important parts.

    @Override
    protected void subAppend(LoggingEvent event) {
        GelfMessage gelf = GelfMessageFactory.makeMessage(layout, event, this);
        this.qw.write(gelf.toJson());
        this.qw.write(Layout.LINE_SEP);

        if (this.immediateFlush) {
            this.qw.flush();
        }
    }
}
