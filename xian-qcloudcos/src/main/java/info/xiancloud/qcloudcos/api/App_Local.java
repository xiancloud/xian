package info.xiancloud.qcloudcos.api;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import info.xiancloud.qcloudcos.api.request.GetObjectRequest;
import info.xiancloud.qcloudcos.api.request.PutObjectRequest;

public class App_Local {

	public static void main(String[] args) throws IOException {

		tGet();
	}

	static void tGet() throws IOException {

		GetObjectRequest fileRequest = new GetObjectRequest("xian", "/xian_runtime_IDE_USER-20170605TK/yyq/hello.jpg", QCloudCosConfig.build());
		byte[] bytes = QCloudCosClient.getObject(fileRequest);
		System.out.println(bytes.length);

		FileOutputStream fos = new FileOutputStream("e:/hello4.jpg");
		fos.write(bytes);
		fos.flush();
		fos.close();
	}

	static void tPut() throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream("e:/hello.jpg");
		byte[] bytes = new byte[1024];
		int len = -1;
		while ((len = in.read(bytes)) > 0) {
			bos.write(bytes, 0, len);
		}
		bos.flush();

		PutObjectRequest fileRequest = new PutObjectRequest("xian", "/yyq/hello.jpg", QCloudCosConfig.build(),
				bos.toByteArray());

		QCloudCosClient.putObject(fileRequest);

		in.close();
	}

}
