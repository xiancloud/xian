package info.xiancloud.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public  class Md5Util {
	public static String getMd5Code(String data) {
		return toMD5(data);
	}

	public static String toMD5(String str){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] byteDigest = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < byteDigest.length; offset++) {
				i = byteDigest[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			//32位加密
			return buf.toString();
			// 16位的加密
			//return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(toMD5("860365039076118"));
	}
}
