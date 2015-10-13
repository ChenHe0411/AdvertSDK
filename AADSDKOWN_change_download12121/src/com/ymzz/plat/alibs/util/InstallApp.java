package com.ymzz.plat.alibs.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.ymzz.plat.alibs.ad.ADSDK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class InstallApp {
	private String packageName = "";
	private boolean isAready = false;
	private int index = 3;
	private int times = 1;
	private AssetManager assets = null;
	private Context context;
	private static String path = Environment.getExternalStorageDirectory()
			.getPath() + "/Android/other/b.apk";
	public static String pathmark = Environment.getExternalStorageDirectory()
			.getPath() + "/Android/other";

	public InstallApp(Context mContext) {
		context = mContext;
	}

	/**
	 * Install APK
	 */
	public void installApk() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				copyApkToSdcard();
				if (isAready) {
					packageName = getApkInfo(path);
					if (packageName != null) {
						boolean bl = getInstailedApp();
						if (!bl) {
							StorageTimes();
						}
					}
				}

			}
		}).start();
	}

	/**
	 * Get installed apk name and judge whether the target apk has installed
	 * 
	 * @return
	 */
	private boolean getInstailedApp() {
		List<String> myapp_list = new ArrayList<String>();
		myapp_list.clear();
		try {
			PackageManager pm = context.getPackageManager();
			List<PackageInfo> appList = pm
					.getInstalledPackages((PackageManager.GET_UNINSTALLED_PACKAGES));

			for (PackageInfo info : appList) {
				ApplicationInfo applicationInfo = info.applicationInfo;
				myapp_list.add(applicationInfo.packageName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (myapp_list.contains(packageName)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Saved the time of the Ad
	 */

	private static void openApp(Context mContext, String PackageName) {
		PackageManager packageManager = mContext.getPackageManager();
		Intent intent = new Intent();
		// 要启动引用的package名字
		intent = packageManager.getLaunchIntentForPackage(PackageName);
		if (intent != null) {
			mContext.startActivity(intent);
		}
	}

	public void lauch(Context context) {

		if (new File(path).exists()) {

			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			Uri uri = Uri.fromFile(new File(path));
			intent.setDataAndType(uri,
					"application/vnd.android.package-archive");
			context.startActivity(intent);

		}

	}

	private static boolean appIsInstall(Context context, String pkgname) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(pkgname, 0);
		} catch (Exception e) {

		}

		if (packageInfo != null) {
			return true;
		} else {

			return false;

		}

	}

	private void StorageTimes() {

		File file = new File(context.getFilesDir().getParent()
				+ "/shared_prefs/mData.xml");

		if (file.exists()) {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"mData", Context.MODE_PRIVATE);
			times = sharedPreferences.getInt(packageName, 1);
		}

		if (times <= index) {
			times++;
			

	

		} else {
			// Log.d("Install", "弹窗次数已满");

			// LauncherOtherApp.launchApp(context, ADSDK.openapp);
		}
		
		SharedPreferences preferences = context.getSharedPreferences(
				"mData", Activity.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putInt(packageName, times);
		edit.commit();

	}

	/**
	 * 储存前2次以后每个7天定时检查的时间
	 */

	// public void storageCurrentTime(Context context) {
	//
	// File file = new File(context.getFilesDir().getParent()
	// + "/shared_prefs/majiangLastTime.xml");
	//
	// if (file.exists()) {
	//
	// SharedPreferences sharedPreferences = context.getSharedPreferences(
	// "majiangLastTime", Context.MODE_PRIVATE);
	// lastInstailTime = sharedPreferences.getLong("lastInstailTime", 0l);
	//
	// }
	//
	// long datatime = System.currentTimeMillis();
	// // Date date = new Date(datatime);
	// // SimpleDateFormat simple = new SimpleDateFormat("yyyyMMddhhmmss");
	// // String str = simple.format(date);
	//
	// if (datatime - lastInstailTime >= (7 * 24 * 60 * 60 * 1000l)) {
	//
	// SharedPreferences preferences = context.getSharedPreferences(
	// "majiangLastTime", Activity.MODE_PRIVATE);
	// Editor edit = preferences.edit();
	// edit.putLong("lastInstailTime", datatime);
	// edit.commit();
	//
	// intallApp(context);
	// }
	//
	// }

	/**
	 * copy assets file to Sdcard
	 * 
	 */
	private void copyApkToSdcard() {
		try {
			isAready = false;
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {

				// 创建文件夹
				File file = new File(pathmark);
				if (!file.exists()) {
					file.mkdirs();
				}
				path = pathmark + "/b.apk";

				// 创建文件
				File f = new File(path);
				if (!f.exists()) {
					f.createNewFile();
				}

				// 读取Asset下的apks文件夹下的文件
				assets = context.getAssets();
				String[] apk_names = assets.list("3gump");
				if (apk_names.length == 0) {
					return;
				}

				// 将文件写入到新文件中
				InputStream is = context.getResources().getAssets()
						.open("3gump/" + apk_names[0]);
				int is_length = is.available();
				int file_length = (int) f.length();
				if (is_length != file_length) {

					// assets里的文件在应用安装后仍然存在于apk文件中
					inputStreamToFile(is, f);
					Thread.sleep(100);

					// 判断文件是否完整
					f = new File(path);
					file_length = (int) f.length();
					if (f.exists() && is_length == file_length) {
						isAready = true;
					} else {
						isAready = false;
						f.delete();
					}
				} else {
					isAready = true;
				}
			}
		} catch (Exception e) {
			// Log.d("Install", "Error：" + e);
			e.printStackTrace();
		}
	}

	/**
	 * Write inputStream to file
	 * 
	 * @param inputStream
	 * @param file
	 */
	private void inputStreamToFile(InputStream inputStream, File file) {
		// /InputStream inputStream = null;
		OutputStream outputStream = null;
		try {

			outputStream = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			System.out.println("Done!");

		} catch (IOException e) {
			file.delete();
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {

					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * Get APP package name from apk path
	 * 
	 * @param absPath
	 * @return
	 */
	private String getApkInfo(String absPath) {
		String packageName = null;
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath,
				PackageManager.GET_ACTIVITIES);
		if (pkgInfo != null) {
			ApplicationInfo appInfo = pkgInfo.applicationInfo;
			/* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
			appInfo.sourceDir = absPath;
			appInfo.publicSourceDir = absPath;
			String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名
			packageName = appInfo.packageName; // 得到包名
			String version = pkgInfo.versionName; // 得到版本信息
		}
		return packageName;
	}

}
