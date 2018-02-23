package biz.paluch.logging.gelf;

/**
 * @author Mark Paluch
 * When {@link biz.paluch.logging.gelf.log4j2.GelfLogField#literal} is provied, it maps to a StaticMessageField.
 */
public class StaticMessageField implements MessageField {

    private String name;
    private String value;

    public StaticMessageField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append(" [name='").append(name).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(']');
        return sb.toString();
    }
}
