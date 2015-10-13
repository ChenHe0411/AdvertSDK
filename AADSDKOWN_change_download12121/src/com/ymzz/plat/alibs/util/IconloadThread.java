package com.ymzz.plat.alibs.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class IconloadThread extends Thread {
	private String downloadUrl;// �������ӵ�ַ

	private String filePath;// �����ļ�·����ַ

	private int downloadLength = 0;
	
	private  boolean prepared_already=false;


	

	public IconloadThread(String downloadUrl, String filePath) {

		this.downloadUrl = downloadUrl;
		this.filePath = filePath;

	}

	@Override
	public void run() {

		try {
			
			URL url = new URL(downloadUrl);

			URLConnection conn = url.openConnection();
			
			int fileSize = conn.getContentLength();
			if (fileSize <= 0) {

				return;
			}

			File localFile = new File(filePath);

			if (localFile.exists() && localFile.length() == fileSize) {
				prepared_already=true;
			} else {

				if (localFile.exists()) {
					localFile.delete();
				}
				byte[] buffer = new byte[4 * 1024];
				int read;
				InputStream in = conn.getInputStream();
				FileOutputStream os = new FileOutputStream(filePath);
				while ((read = in.read(buffer)) > 0) {
					os.write(buffer, 0, read);

					downloadLength += read;
				}

				if (downloadLength == fileSize) {
					File file2 = new File(filePath);

					if (!file2.exists()) {
						
						prepared_already=false;
					} else {

						
						prepared_already=true;

					}
				} else {
					File file2 = new File(filePath);
					if (file2.exists()) {

						file2.delete();
					}
					prepared_already=false;

				}

				os.close();
				in.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
