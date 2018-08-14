package info.xiancloud.dao.core.model.ddl;

public class Para {
	private String key;
	private Operator.Type type;
	private Object value;

	public Para(String key, Operator.Type type) {
		this.key = key;
		this.type = type;
	}

	public Para(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Para(String key, Operator.Type type, Object value) {
		this.key = key;
		this.type = type;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public Operator.Type getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
}
