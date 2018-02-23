package org.graylog2;

public class GelfSenderResult {

    public static final int ERROR_CODE = -1;

    public static final GelfSenderResult OK = new GelfSenderResult(1, null);
    public static final GelfSenderResult MESSAGE_NOT_VALID_OR_SHUTTING_DOWN = new GelfSenderResult(11, null);
    public static final GelfSenderResult MESSAGE_NOT_VALID = new GelfSenderResult(12, null);

    private int code;

    private Exception e;

    public GelfSenderResult(int code, Exception e) {
        this.code = code;
        this.e = e;
    }

    public int getCode() {
        return code;
    }

    public Exception getException() {
        return e;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GelfSenderResult that = (GelfSenderResult) o;

        return code == that.code;

    }

    @Override
    public int hashCode() {
        return code;
    }

    @Override
    public String toString() {
        return "GelfSenderResult{" +
                "code=" + code +
                ", e=" + e +
                '}';
    }
}
