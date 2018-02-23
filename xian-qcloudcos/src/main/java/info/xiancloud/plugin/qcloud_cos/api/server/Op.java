package info.xiancloud.plugin.qcloud_cos.api.server;

public enum Op {

	PUT,
	GET;

	public String lowerName() {
		return this.name().toLowerCase();
	}
}
