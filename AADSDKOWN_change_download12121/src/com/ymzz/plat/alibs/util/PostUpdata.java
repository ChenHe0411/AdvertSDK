package com.ymzz.plat.alibs.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;




import android.util.Log;

public class PostUpdata {
	public static String readContentFromPost(String url,String info) throws IOException {
		URL postUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) postUrl
				.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.connect();
		DataOutputStream out = new DataOutputStream(
				connection.getOutputStream());
		String content = info;
		out.writeBytes(content);
		out.flush();
		out.close(); // flush and close
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "utf-8"));
		String line = "";
		String ss = "";
		while ((ss = reader.readLine()) != null) {
			line = line + ss;
		}
		reader.close();
		connection.disconnect();
		return line;
	}
}
