package info.xiancloud.plugin.httpclient.http;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import org.apache.http.ParseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;

import info.xiancloud.plugin.httpclient.apache_http.IApacheHttpClient;
import info.xiancloud.plugin.httpclient.apache_http.no_auth.ApacheHttpClient;
import info.xiancloud.plugin.httpclient.apache_http.pool.ApacheHttpConnManager;
import info.xiancloud.plugin.util.LOG;

/**
 * 暂时测试用类---
 * 
 * @author yyq
 *
 */
public class App {

	public static void main(String[] args) throws Exception {

		for(int i=0;i<11;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						IApacheHttpClient httpClient = ApacheHttpClient
								.newInstance("http://192.168.234.130:8080/httpsweb/login", new HashMap<>());
						try {
							System.out.println(EntityUtils.toString(httpClient.getHttpResponse().getEntity(), "UTF-8"));
						} catch (ConnectTimeoutException e) {
							LOG.error(e);
							e.printStackTrace();
						} catch (SocketTimeoutException e) {
							LOG.error(e);
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							LOG.error(e);
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							LOG.error(e);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							httpClient.close();
						} catch (IOException e) {
							LOG.error(e);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							LOG.error(e);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.err.println(String.format("-avaliable:[%s],leased:[%s],pending:[%s]",
								ApacheHttpConnManager.getAvailable(), ApacheHttpConnManager.getLeased(),
								ApacheHttpConnManager.getPending()));
					}
				}
			}).start();
		}
		
		Thread.sleep(1000000000);

	}

	static void https() {
		// String cerPath="e:/ssl/yyq.jks";
		// InputStream cerIn=new FileInputStream(cerPath);

		// http://localhost:8080/httpsweb/login
		// https://localhost/httpsweb/login
		// Response result = HttpKit.post("https://localhost/httpsweb/login")
		// .addParam("name", "袁浴钦")
		// .addHeader("token", "123")
		// .setSSL(null,null)
		// .setContent("yyq")
		// .executeLocal();
		// System.out.println(result.string());
		// System.out.println(result);

		// ObjectInputStream ois=new ObjectInputStream(new
		// ByteArrayInputStream(Base64.getDecoder().decode(result)));
		// UnitRequest request=(UnitRequest) ois.readObject();
		// System.out.println(request);

		/*
		 * String tStr=JSON.toJSONString(JSON.toJSONString(result));
		 * System.out.println(tStr); byte[] tt=JSON.parseObject(tStr,
		 * byte[].class); System.out.println(tt);
		 */

		/*
		 * Map<String,Object> map=new HashMap<String,Object>();
		 * map.put("name","yyq"); map.put("content", result);
		 * 
		 * String mapStr=JSON.toJSONString(map);
		 * 
		 * Map<String, Object> nMap= JSON.parseObject(mapStr, Map.class);
		 * 
		 * ObjectInputStream ois=new ObjectInputStream(new
		 * ByteArrayInputStream(result)); UnitRequest request=(UnitRequest)
		 * ois.readObject(); request.generateBody();
		 * System.out.println(request);
		 */
	}
}
