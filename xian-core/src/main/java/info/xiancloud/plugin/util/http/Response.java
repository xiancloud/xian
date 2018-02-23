package info.xiancloud.plugin.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import info.xiancloud.plugin.util.LOG;

public class Response implements Serializable {

	private  String defaultCharset = "UTF-8";

	/**
	 * 
	 */
	private static final long serialVersionUID = 6004728092617940783L;

	private int status;
	private Map<String, String> headers;
	private String body;

	/**
	 * 读取返回结果字符
	 * 
	 * @return
	 */
	public String string() {
		return body;

	}

	/**
	 * 设置响应消息体解析编码
	 * 
	 * @param charset
	 */
	protected void setBodyCharset(String charset) {
		defaultCharset = charset;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	protected void setHeaders(Map<String, List<String>> headerFields) {
		headers = new HashMap<>();
		/**
		 * 获取响应头
		 */
		Set<Entry<String, List<String>>> entrySet = headerFields.entrySet();
		Iterator<Entry<String, List<String>>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, List<String>> next = iterator.next();
			String key = next.getKey();
			List<String> value = next.getValue();
			if (key == null) {
				// TODO
				// System.out.println("--");
			} else
				// System.out.println(key + ":" + value.toString());
				headers.put(key, value.toString());
		}

	}

	public void setBody(InputStream body) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(body, defaultCharset));
			StringBuilder result = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			this.body = result.toString();
		} catch (Exception e) {
			LOG.error(e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				br = null;
			}
			if (body != null) {
				try {
					body.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				body = null;
			}
		}
	}

}
