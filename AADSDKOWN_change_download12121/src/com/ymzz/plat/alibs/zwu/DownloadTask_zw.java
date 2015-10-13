package com.ymzz.plat.alibs.zwu;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 广告帮助类
 * 
 * @author Woody
 * 
 */
public class DownloadTask_zw extends Thread {

	public static String src = "";
	public static String md5 = "";
	public static boolean isAdReady = false;
	private String html = null;
	private String url = null;
	private Map<String, String> map;
	private Map<String, String> tempMap;
	private InputStream input;
	private List<String> list;
	public Handler handler;

	public DownloadTask_zw(List<String> list, String mUrl) {
		this.list = list;
		this.url = mUrl;
	}

	@Override
	public void run() {

		try {

			if (url != null && !url.equals("")) {
				md5 = FileUtil.getMD5(url.getBytes());
				if (!list.contains(url)) {
					list.add(url);
					// 获取Html5的页面字符串
					html = getHtml5Page();
					// MyLogcat.log("页面内容：" + html);
					if (html != null && !html.equals("")) {
						// 匹配Html页面的相关数据
						matchHtml5();
						// 下载所有的相关数据
						downloadHtmlData();
						// 替换特定的字符串
						repAll();
						if (list.contains(url)) {
							list.remove(url);
					
							isAdReady = true;
						}
					}
				}
			}
		} catch (Exception e) {
			if (list.contains(url)) {
				list.remove(url);
				isAdReady = false;
			}
			e.printStackTrace();
		}
		super.run();
	}

	/**
	 * 重加载
	 */
	public void loadAd() {
		run();
	}

	/**
	 * 下载页面上的所有数据到本地
	 */
	private void downloadHtmlData() {
		try {
			if (map != null) {
				// 遍历map
				Iterator<Map.Entry<String, String>> it = map.entrySet()
						.iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> entry = it.next();
					// MyLogcat.log("键：" + entry.getKey());
					// MyLogcat.log("值：" + entry.getValue());
					beginTask(entry.getKey(), entry.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始下载任务
	 * 
	 * @param key
	 * @param value
	 */
	private void beginTask(String key, String value) {
		try {
			FileUtil.saveOtherFile(key, getPath(value), value, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取本地绝对路径
	 * 
	 * @param content
	 * @return
	 */
	private String getPath(String content) {
		String str = "";
		for (int k = 0; k < content.split("/").length - 1; k++) {
			str += content.split("/")[k] + "/";
		}
		str.substring(0, str.length() - 1);
		return str;
	}

	/**
	 * 根据路径获取文件名
	 * 
	 * @param content
	 * @return
	 */
	private String getFileName(String content) {
		return content.split("/")[content.split("/").length - 1];
	}

	/**
	 * 匹配对应的标签
	 * 
	 * @return
	 */
	private void matchHtml5() {
		// css的正则表达式
		String p1 = "(?s)<link[^>]+?href=.([^>]+?)\"";
		// script的正则表达式
		String p2 = "(?s)<(script|img)[^>]+?src=.([^>]+?)(\"|\')";
		// 实例化map
		map = new HashMap<String, String>();
		tempMap = new HashMap<String, String>();
		// 初始化文件夹
		FileUtil.creatSDDir(FileUtil.FILE_ROOT + md5 + "/");
		// 放入页面
		map.put(url, FileUtil.FILE_ROOT + md5 + ".html");
		// 加载CSS页面
		matchAll(html, p1, 1);
		// 加载JS页面和图片文件
		matchAll(html, p2, 2);
	}

	/**
	 * 匹配对应的字符串
	 * 
	 * @param s
	 * @param p
	 * @param group
	 * @return
	 */
	private void matchAll(String s, String p, int group) {
		Pattern pattern = Pattern.compile(p);
		Matcher m = pattern.matcher(s);
		while (m.find()) {
			// 要下载的文件在本地的绝对路径，如 /storage/testM/adfolde/index.png
			String str1 = FileUtil.FILE_ROOT + md5 + "/"
					+ getFileName(m.group(group));

			// 优化相对路径
			String str2 = m.group(group).replace("../", "").replace("./", "");
			if (str2.startsWith("/")) {
				str2 = str2.substring(1, str2.length() - 1);
			}

			// 如果资源文件是绝对路径，带http开头的
			if (m.group(group).startsWith("http")) {
				map.put(m.group(group), str1);
				// MyLogcat.log("下载链接-------键为：" + m.group(group) + ";值为：" +
				// str1
				// + "\n");
				tempMap.put(m.group(group), str1);
				// MyLogcat.log("替换链接-------键为：" + m.group(group) + ";值为：" +
				// str1
				// + "\n");
			}
			// 如果资源文件是相对路径，如./page/index.png, ../page/index.png
			else {
				map.put(getPath(url) + str2, FileUtil.FILE_ROOT + md5 + "/"
						+ str2);
				// MyLogcat.log("下载链接--------键为：" + getPath(url) + str2 + ";值为："
				// + FileUtil.FILE_ROOT + md5 + "/" + str2 + "\n");
				tempMap.put(m.group(group), FileUtil.FILE_ROOT + md5 + "/"
						+ str2);
				// MyLogcat.log("替换链接--------键为：" + m.group(group) + ";值为："
				// + FileUtil.FILE_ROOT + md5 + "/" + str2 + "\n");
			}
		}
	}

	/**
	 * 替换对应的字符串
	 */
	private void repAll() {
		if (map != null) {
			// 遍历map
			Iterator<Map.Entry<String, String>> it = tempMap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				MyLogcat.log("替换键：" + entry.getKey());
				MyLogcat.log("替换值：" + entry.getValue());
				if (html.contains(entry.getKey())) {
					MyLogcat.log("页面存在：" + entry.getKey());
				}
				html = html.replace(entry.getKey(), entry.getValue());
			}
			// MyLogcat.log("新的页面：\n" + tempHtml);
			// 重新保存Html文件
			try {
				input = FileUtil.StringTOInputStream(html);
				FileUtil.saveOtherFile(url, FileUtil.FILE_ROOT + md5,
						FileUtil.FILE_ROOT + md5 + ".html", input);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取Html页面
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getHtml5Page() throws Exception {
		int k = 0;
		if (url.equals("") && k != 1) {
			getHtml5Page();
			k++;
		}
		return getH5String(url);
	}

	/**
	 * 获取H5页面字符串
	 * 
	 * @param urlpath
	 * @return
	 * @throws Exception
	 */
	public static String getH5String(String urlpath) throws Exception {
		String html = null;
		if (urlpath == null) {
			return null;
		}
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);

		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			byte[] data = readStream(inputStream);
			html = new String(data);
		}
		conn.disconnect();
		return html;
	}

	/**
	 * 读取Html页面信息
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		while ((len = inputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, len);
		}

		inputStream.close();
		byteArrayOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}
}
