package org.graylog2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

public class GelfMessage {

    private static final String ID_NAME = "id";
    private static final String GELF_VERSION = "1.1";
    private static final byte[] GELF_CHUNKED_ID = new byte[]{0x1e, 0x0f};
    private static final int MAXIMUM_CHUNK_SIZE = 1420;
    private static final BigDecimal TIME_DIVISOR = new BigDecimal(1000);

    private String version = GELF_VERSION;
    /**
     * host被gelfj预留为关键属性，然后以source属性录入到grayog内了，所以gelfJSONAppender发送json串日志时，不能使用host属性
     */
    private String host;
    private byte[] hostBytes = lastFourAsciiBytes("none");
    private String shortMessage;
    private String fullMessage;
    private long javaTimestamp;
    private String level;
    private String facility = "gelf-java";
    private String line;
    private String file;
    private Map<String, Object> additonalFields = new HashMap<String, Object>();

    public GelfMessage() {
    }

    public GelfMessage(String shortMessage, String fullMessage, long timestamp, String level) {
        this(shortMessage, fullMessage, timestamp, level, null, null);
    }

    public GelfMessage(String shortMessage, String fullMessage, long timestamp, String level, String line, String file) {
        this.shortMessage = shortMessage != null ? shortMessage : "null";
        this.fullMessage = fullMessage;
        this.javaTimestamp = timestamp;
        this.level = level;
        this.line = line;
        this.file = file;
    }

    public String toJson() {
        JSONObject map = new JSONObject();

        map.put("version", getVersion());
        map.put("host", getHost());
        map.put("short_message", getShortMessage());
        map.put("full_message", getFullMessage());
        map.put("timestamp", getTimestamp());

        map.put("facility", getFacility());
        try {
            map.put("level", Long.parseLong(getLevel()));
        } catch (NumberFormatException e) {
            map.put("level", 6L); // fallback to info
        }

        if (null != getFile()) {
            map.put("file", getFile());
        }
        if (null != getLine()) {
            try {
                map.put("line", Long.parseLong(getLine()));
            } catch (NumberFormatException e) {
                map.put("line", -1L);
            }
        }

        for (Map.Entry<String, Object> additionalField : additonalFields.entrySet()) {
            if (!ID_NAME.equals(additionalField.getKey())) {
                map.put("_" + additionalField.getKey(), additionalField.getValue());
            }
        }

        return map.toJSONString();
    }

    public ByteBuffer[] toUDPBuffers() {
        byte[] messageBytes = gzipMessage(toJson());
        // calculate the length of the datagrams array
        int diagrams_length = messageBytes.length / MAXIMUM_CHUNK_SIZE;
        // In case of a remainder, due to the integer division, add a extra datagram
        if (messageBytes.length % MAXIMUM_CHUNK_SIZE != 0) {
            diagrams_length++;
        }
        ByteBuffer[] datagrams = new ByteBuffer[diagrams_length];
        if (messageBytes.length > MAXIMUM_CHUNK_SIZE) {
            sliceDatagrams(messageBytes, datagrams);
        } else {
            datagrams[0] = ByteBuffer.allocate(messageBytes.length);
            datagrams[0].put(messageBytes);
            datagrams[0].flip();
        }
        return datagrams;
    }

    public ByteBuffer toTCPBuffer() {
        byte[] messageBytes;
        try {
            // Do not use GZIP, as the headers will contain \0 bytes
            // graylog2-server uses \0 as a delimiter for TCP frames
            // see: https://github.com/Graylog2/graylog2-server/issues/127
            String json = toJson();
            json += '\0';
            messageBytes = json.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("No UTF-8 support available.", e);
        }

        ByteBuffer buffer = ByteBuffer.allocate(messageBytes.length);
        buffer.put(messageBytes);
        buffer.flip();
        return buffer;
    }

    public ByteBuffer toAMQPBuffer() {
        byte[] messageBytes = gzipMessage(toJson());
        ByteBuffer buffer = ByteBuffer.allocate(messageBytes.length);
        buffer.put(messageBytes);
        buffer.flip();
        return buffer;
    }

    private void sliceDatagrams(byte[] messageBytes, ByteBuffer[] datagrams) {
        int messageLength = messageBytes.length;
        byte[] messageId = new byte[8];
        new Random().nextBytes(messageId);

        // Reuse length of datagrams array since this is supposed to be the correct number of datagrams
        int num = datagrams.length;
        for (int idx = 0; idx < num; idx++) {
            byte[] header = concatByteArray(GELF_CHUNKED_ID, concatByteArray(messageId, new byte[]{(byte) idx, (byte) num}));
            int from = idx * MAXIMUM_CHUNK_SIZE;
            int to = from + MAXIMUM_CHUNK_SIZE;
            if (to >= messageLength) {
                to = messageLength;
            }

            byte[] range = new byte[to - from];
            System.arraycopy(messageBytes, from, range, 0, range.length);

            byte[] datagram = concatByteArray(header, range);
            datagrams[idx] = ByteBuffer.allocate(datagram.length);
            datagrams[idx].put(datagram);
            datagrams[idx].flip();
        }
    }

    public int getCurrentMillis() {
        return (int) System.currentTimeMillis();
    }

    private byte[] gzipMessage(String message) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            GZIPOutputStream stream = new GZIPOutputStream(bos);
            byte[] bytes;
            try {
                bytes = message.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("No UTF-8 support available.", e);
            }
            stream.write(bytes);
            stream.finish();
            stream.close();
            byte[] zipped = bos.toByteArray();
            bos.close();
            return zipped;
        } catch (IOException e) {
            return null;
        }
    }

    private byte[] lastFourAsciiBytes(String host) {
        final String shortHost = host.length() >= 4 ? host.substring(host.length() - 4) : host;
        try {
            return shortHost.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("JVM without ascii support?", e);
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        this.hostBytes = lastFourAsciiBytes(host);
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }

    public String getTimestamp() {
        return new BigDecimal(javaTimestamp).divide(TIME_DIVISOR).toPlainString();
    }

    public Long getJavaTimestamp() {
        return javaTimestamp;
    }

    public void setJavaTimestamp(long javaTimestamp) {
        this.javaTimestamp = javaTimestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public GelfMessage addField(String key, String value) {
        getAdditonalFields().put(key, value);
        return this;
    }

    public GelfMessage addField(String key, Object value) {
        getAdditonalFields().put(key, value);
        return this;
    }

    public Map<String, Object> getAdditonalFields() {
        return additonalFields;
    }

    public void setAdditonalFields(Map<String, Object> additonalFields) {
        this.additonalFields = new HashMap<String, Object>(additonalFields);
    }

    public boolean isValid() {
        return isShortOrFullMessagesExists() && !isEmpty(version) && !isEmpty(host) && !isEmpty(facility);
    }

    private boolean isShortOrFullMessagesExists() {
        return shortMessage != null || fullMessage != null;
    }

    public boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    byte[] concatByteArray(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
