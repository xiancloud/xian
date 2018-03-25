package info.xiancloud.dao.global;

public class Para {
	private String key;
	private Cnd.Type type;
	private Object value;

	public Para(String key, Cnd.Type type) {
		this.key = key;
		this.type = type;
	}

	public Para(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Para(String key, Cnd.Type type, Object value) {
		this.key = key;
		this.type = type;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public Cnd.Type getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
}
