package info.xiancloud.qcloudcos.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class App_Netty {

	public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("image/*");
	private final static OkHttpClient client = new OkHttpClient.Builder()
			.addNetworkInterceptor(new LoggingInterceptor()).build();

	public static void main(String[] args) throws IOException {
		put();
	}

	static void get() throws IOException {

		File file = new File("e:\\hello12.jpg");
		FileOutputStream fos=new FileOutputStream(file);

		Request request = new Request.Builder()
				.url("http://127.0.0.1:8080?op=get&bucketName=xian&cosPath=/xian_runtime_IDE_USER-20170605TK/yyq/netty.jpg").get()
				.get()
				.build();
		Response response = client.newCall(request).execute();

		if (!response.isSuccessful())
			throw new IOException("Unexpected code " + response);

		System.out.println("请求成功了...");
		fos.write(response.body().bytes());
	}

	static void put() throws IOException {
		File file = new File("e:\\hello.jpg");
		
		Request request = new Request.Builder()
				.url("http://127.0.0.1:8080?op=put&bucketName=xian&cosPath=/yyq/netty.jpg")
				.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
				// .header("Content-Length", file.length()+"")
				.build();
		Response response = client.newCall(request).execute();
		
		if (!response.isSuccessful())
			throw new IOException("Unexpected code " + response);

		System.out.println("请求成功了...");
		System.out.println(response.body().string());
	}

    static void getRemote() throws IOException{
    	File file = new File("e:\\hello66.jpg");
		FileOutputStream fos=new FileOutputStream(file);

		Request request = new Request.Builder()
				.url("http://cos3.apaycloud.com:19323?op=get&bucketName=xian&cosPath=/xian_runtime_IDE_USER-20170605TK/yyq/netty.jpg").get()
				.get()
				.build();
		Response response = client.newCall(request).execute();

		if (!response.isSuccessful())
			throw new IOException("Unexpected code " + response);

		System.out.println("请求成功了...");
		fos.write(response.body().bytes());
    }
	
    static void putRemote() throws IOException{
		File file = new File("e:\\hello.jpg");

		Request request = new Request.Builder()
				.url("http://cos3.apaycloud.com:19323?bucketName=yyq&cosPath=/yyq/netty.jpg&op=put")
				.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
				// .header("Content-Length", file.length()+"")
				.build();
		Response response = client.newCall(request).execute();

		if (!response.isSuccessful())
			throw new IOException("Unexpected code " + response);

		System.out.println("请求成功了...");
		System.out.println(response.body().string());
	}
}

class LoggingInterceptor implements Interceptor {
	@Override
	public Response intercept(Interceptor.Chain chain) throws IOException {
		Request request = chain.request();

		long t1 = System.nanoTime();
		System.out.println(
				String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

		Response response = chain.proceed(request);

		long t2 = System.nanoTime();
		System.out.println(String.format("Received response for %s in %.1fms%n%s", response.request().url(),
				(t2 - t1) / 1e6d, response.headers()));

		return response;
	}
}