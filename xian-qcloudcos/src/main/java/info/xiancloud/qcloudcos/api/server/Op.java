package info.xiancloud.qcloudcos.api.server;

public enum Op {

	PUT,
	GET;

	public String lowerName() {
		return this.name().toLowerCase();
	}
}
