package com.ymzz.plat.alibs.zwu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Get请求
 * 
 * @author zw
 * 
 */
public class HttpGetUtil {

	private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时5秒钟
	private static final int SO_TIMEOUT = 5 * 1000; // 设置等待数据超时时间5秒钟
	private static URL url = null;
	private static HttpURLConnection conn = null;

	public static String get(String sendUrl) {
		String body = null;
		try {
			url = new URL(sendUrl);
			MyLogcat.log("" + url);
			conn = (HttpURLConnection) url.openConnection();
			// 设置连接属性
			// 使用 URL 连接进行输出
			conn.setDoOutput(true);
			// 使用 URL 连接进行输入
			conn.setDoInput(true);
			// 忽略缓存
			conn.setUseCaches(false);
			// 设置连接超时时长，单位毫秒
			conn.setConnectTimeout(REQUEST_TIMEOUT);
			// 设置读取时长，单位毫秒
			conn.setReadTimeout(SO_TIMEOUT);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// 设置请求方式，POST or GET，注意：如果请求地址为一个servlet地址的话必须设置成POST方式
			conn.setRequestMethod("GET");
			BufferedReader bufferReader = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "utf-8"));
			String lines = "";
			StringBuffer bs = new StringBuffer();
			while ((lines = bufferReader.readLine()) != null) {
				bs.append(lines);
			}
			body = bs.toString();
		} catch (Exception e) {
			e.printStackTrace();
			MyLogcat.log("" + e);
		}
		return body;
	}
}
