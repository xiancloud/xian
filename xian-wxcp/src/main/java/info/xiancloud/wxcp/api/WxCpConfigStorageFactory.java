package info.xiancloud.wxcp.api;

public class WxCpConfigStorageFactory {

	private static volatile WxCpConfigStorage wxCpConfigStorage;

	public static WxCpConfigStorage build() {
		if (wxCpConfigStorage == null) {
			synchronized (WxCpConfigStorageFactory.class) {
				if (wxCpConfigStorage == null) {
					wxCpConfigStorage = new WxCpInMemoryConfigStorage();
				}
				return wxCpConfigStorage;
			}
		}
		return wxCpConfigStorage;
	}
}
