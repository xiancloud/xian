package org.graylog2.log;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.util.LOG;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.graylog2.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anton Yakimov
 * @author Jochen Schalanda
 */
public class GelfAppender extends AppenderSkeleton implements GelfMessageProvider {

    private String graylogHost;
    private String amqpURI;
    private String amqpExchangeName;
    private String amqpRoutingKey;
    private int amqpMaxRetries = 0;
    private static String originHost;
    private int graylogPort = 12201;
    private String facility;
    private GelfSender gelfSender;
    private boolean extractStacktrace;
    private boolean addExtendedInformation;
    private boolean includeLocation = true;
    private Map<String, String> fields;

    public GelfAppender() {
        super();
    }

    @SuppressWarnings("unchecked")
    public void setAdditionalFields(String additionalFields) {
        fields = (Map<String, String>) JSONObject.parse(additionalFields.replaceAll("'", "\""));
    }

    public int getGraylogPort() {
        return graylogPort;
    }

    public void setGraylogPort(int graylogPort) {
        this.graylogPort = graylogPort;
    }

    public String getGraylogHost() {
        return graylogHost;
    }

    public void setGraylogHost(String graylogHost) {
        this.graylogHost = graylogHost;
    }

    public String getAmqpURI() {
        return amqpURI;
    }

    public void setAmqpURI(String amqpURI) {
        this.amqpURI = amqpURI;
    }

    public String getAmqpExchangeName() {
        return amqpExchangeName;
    }

    public void setAmqpExchangeName(String amqpExchangeName) {
        this.amqpExchangeName = amqpExchangeName;
    }

    public String getAmqpRoutingKey() {
        return amqpRoutingKey;
    }

    public void setAmqpRoutingKey(String amqpRoutingKey) {
        this.amqpRoutingKey = amqpRoutingKey;
    }

    public int getAmqpMaxRetries() {
        return amqpMaxRetries;
    }

    public void setAmqpMaxRetries(int amqpMaxRetries) {
        this.amqpMaxRetries = amqpMaxRetries;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public boolean isExtractStacktrace() {
        return extractStacktrace;
    }

    public void setExtractStacktrace(boolean extractStacktrace) {
        this.extractStacktrace = extractStacktrace;
    }

    public String getOriginHost() {
        if (originHost == null) {
            originHost = getLocalHostName();
        }
        return originHost;
    }

    private String getLocalHostName() {
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            errorHandler.error("Unknown local hostname", e, ErrorCode.GENERIC_FAILURE);
        }

        return hostName;
    }

    public void setOriginHost(String originHost) {
        GelfAppender.originHost = originHost;
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

    @Override
    public void activateOptions() {
        if (graylogHost == null && amqpURI == null) {
            errorHandler.error("Graylog2 hostname and amqp uri are empty!", null, ErrorCode.WRITE_FAILURE);
        } else if (graylogHost != null && amqpURI != null) {
            errorHandler.error("Graylog2 hostname and amqp uri are both informed!", null, ErrorCode.WRITE_FAILURE);
        } else {
            try {
                if (graylogHost != null && graylogHost.startsWith("tcp:")) {
                    String tcpGraylogHost = graylogHost.substring(4);
                    gelfSender = getGelfTCPSender(tcpGraylogHost, graylogPort);
                } else if (graylogHost != null && graylogHost.startsWith("udp:")) {
                    String udpGraylogHost = graylogHost.substring(4);
                    gelfSender = getGelfUDPSender(udpGraylogHost, graylogPort);
                } else if (amqpURI != null) {
                    LOG.info("AMQP protocol is not supported.");
                    /*gelfSender = getGelfAMQPSender(amqpURI, amqpExchangeName, amqpRoutingKey, amqpMaxRetries);*/
                } else {
                    gelfSender = getGelfUDPSender(graylogHost, graylogPort);
                }
            } catch (UnknownHostException e) {
                errorHandler.error("Unknown Graylog2 hostname:" + getGraylogHost(), e, ErrorCode.WRITE_FAILURE);
            } catch (SocketException e) {
                errorHandler.error("Socket exception", e, ErrorCode.WRITE_FAILURE);
            } catch (IOException e) {
                errorHandler.error("IO exception", e, ErrorCode.WRITE_FAILURE);
            }/* catch (URISyntaxException e) {
                errorHandler.error("AMQP uri exception", e, ErrorCode.WRITE_FAILURE);
            } catch (NoSuchAlgorithmException e) {
                errorHandler.error("AMQP algorithm exception", e, ErrorCode.WRITE_FAILURE);
            } catch (KeyManagementException e) {
                errorHandler.error("AMQP key exception", e, ErrorCode.WRITE_FAILURE);
            }*/
        }
    }

    protected GelfUDPSender getGelfUDPSender(String udpGraylogHost, int graylogPort) throws IOException {
        return new GelfUDPSender(udpGraylogHost, graylogPort);
    }

    protected GelfTCPSender getGelfTCPSender(String tcpGraylogHost, int graylogPort) throws IOException {
        return new GelfTCPSender(tcpGraylogHost, graylogPort);
    }

    /*protected GelfAMQPSender getGelfAMQPSender(String amqpURI, String amqpExchangeName, String amqpRoutingKey, int amqpMaxRetries) throws IOException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        return new GelfAMQPSender(amqpURI, amqpExchangeName, amqpRoutingKey, amqpMaxRetries);
    }*/

    @Override
    protected void append(LoggingEvent event) {
        GelfMessage gelfMessage = GelfMessageFactory.makeMessage(layout, event, this);

        if (getGelfSender() == null) {
            errorHandler.error("Could not send GELF message. Gelf Sender is not initialised and equals null");
        } else {
            GelfSenderResult gelfSenderResult = getGelfSender().sendMessage(gelfMessage);
            if (!GelfSenderResult.OK.equals(gelfSenderResult)) {
                errorHandler.error("Error during sending GELF message. Error code: " + gelfSenderResult.getCode() + ".", gelfSenderResult.getException(), ErrorCode.WRITE_FAILURE);
            }
        }

    }

    public GelfSender getGelfSender() {
        return gelfSender;
    }

    public void close() {
        GelfSender x = this.getGelfSender();
        if (x != null) {
            x.close();
        }
    }

    public boolean requiresLayout() {
        return true;
    }
}
