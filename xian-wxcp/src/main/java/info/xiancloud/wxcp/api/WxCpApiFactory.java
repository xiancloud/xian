package info.xiancloud.wxcp.api;

public class WxCpApiFactory {

	private static volatile WxCpApi wxCpApi;

	public static WxCpApi buildWxCpApi() {
		if (wxCpApi == null) {
			synchronized (WxCpApiFactory.class) {
				if (wxCpApi == null) {
					wxCpApi = new WxCpApiImpl();
					wxCpApi.setWxCpConfigStorage(WxCpConfigStorageFactory.build());
				}
				return wxCpApi;
			}
		}
		return wxCpApi;
	}
}
