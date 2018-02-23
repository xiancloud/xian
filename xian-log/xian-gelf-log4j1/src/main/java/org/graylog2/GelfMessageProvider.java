package org.graylog2;

import java.util.Map;

public interface GelfMessageProvider {
    public boolean isExtractStacktrace();
    public String getOriginHost();
    public String getFacility();
    public Map<String, String> getFields();
    public boolean isAddExtendedInformation();
    public boolean isIncludeLocation();
    public Object transformExtendedField(String field, Object object);
}
