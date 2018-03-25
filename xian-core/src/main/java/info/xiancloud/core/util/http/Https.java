package info.xiancloud.core.util.http;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;


class Https {

	public static SSLSocketFactory getSslSocketFactory(InputStream cerIn, String storePass) {
		SSLSocketFactory sslSocketFactory = null;
		try {
			TrustManager[] trustManagers = prepareTrustManager(cerIn, storePass);
			X509TrustManager manager;

			// 优先使用自定义的证书管理器
			if (trustManagers != null) {
				manager = chooseTrustManager(trustManagers);
				LOG.debug("---https访问，使用自定义证书---");
			} else {
				// 否则使用无证书认证的证书管理器
				manager = UnSafeTrustManager;
				LOG.debug("---https访问，无证书---");
			}
			// 创建TLS类型的SSLContext对象
			SSLContext sslContext = SSLContext.getInstance("TLS");
			// 用上面得到的trustManagers初始化SSLContext，这样sslContext就会信任keyStore中的证书
			// 第一个参数是授权的密钥管理器，用来授权验证，比如授权自签名的证书验证。第二个是被授权的证书管理器，用来验证服务器端的证书
			sslContext.init(null, new TrustManager[] { manager }, null);
			// 通过sslContext获取SSLSocketFactory对象
			sslSocketFactory = sslContext.getSocketFactory();
			return sslSocketFactory;
		} catch (Exception e) {
			//LOG.error("--证书加载出错-", e);
			throw new RuntimeException("证书信息加载错误");
		}
	}

	public static TrustManager[] prepareTrustManager(InputStream cerIn, String cerPass) throws Exception {
		//FIXME
		if (cerIn == null || StringUtil.isEmpty(cerPass))
			return null;
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(cerIn, cerPass.toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
		tmf.init(ks);
		TrustManager[] tms = tmf.getTrustManagers();
		return tms;
	}

	/**
	 * 选择指定格式的证书管理器
	 * 
	 * @param trustManagers
	 * @return
	 */
	public static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
		for (TrustManager trustManager : trustManagers) {
			if (trustManager instanceof X509TrustManager) {
				return (X509TrustManager) trustManager;
			}
		}
		return null;
	}

	/**
	 * 默认无证书
	 */
	public static X509TrustManager UnSafeTrustManager = new X509TrustManager() {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}
	};

	/**
	 * 远程主机验证
	 */
	public static HostnameVerifier UnSafeHostnameVerifier = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
}
