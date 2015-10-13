package com.ymzz.plat.alibs.util;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class PicDownloadThreadself extends Thread {
	private String downloadUrl;// �������ӵ�ַ

	private String filePath;// �����ļ�·����ַ

	private int downloadLength = 0;
	
	public  boolean prepared_already=false;

	// private String thread_fname;
	// private String thread_fPath;
	// public downloadPic(String downloadUrl, String filePath,
	// String fname, String fPath) {
	//
	// this.downloadUrl = downloadUrl;
	// this.filePath = filePath;
	// this.thread_fname = fname;
	// this.thread_fPath = fPath;
	// }
	
	

	public PicDownloadThreadself(String downloadUrl, String filePath) {

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

	// public static void downloadFile(String url, String fileDirPath, String
	// fileFullName)
	// throws IOException {
	// URL theURL = new URL(url);
	// URLConnection con = theURL.openConnection();
	//
	// if (fileFullName != null) {
	// byte[] buffer = new byte[4 * 1024];
	// int read;
	//
	// File fileFolder = new File(fileDirPath);
	// if (!fileFolder.exists()) {
	// fileFolder.mkdirs();
	// }
	// InputStream in = con.getInputStream();
	//
	// String fileFullNamePath=fileDirPath + "/"
	// + fileFullName;
	// File file=new File(fileFullNamePath);
	// if (file.exists()) {
	// file.delete();
	// }
	// FileOutputStream os = new FileOutputStream(fileFullNamePath);
	// while ((read = in.read(buffer)) > 0) {
	// os.write(buffer, 0, read);
	// }
	// os.close();
	// in.close();
	// }
	// }
}
