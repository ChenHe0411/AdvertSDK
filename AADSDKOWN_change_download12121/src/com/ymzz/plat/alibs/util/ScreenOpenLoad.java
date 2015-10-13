package com.ymzz.plat.alibs.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.feilu.download.StorageUtils;
import com.ymzz.plat.alibs.ad.PopupService;
import com.ymzz.plat.alibs.util.AddOtherAppShortcutUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ScreenOpenLoad {

	public static String filePathfile = "filePathxml";

	public static void storeAppNameData(Context context, String filePath,
			int time) {
		SharedPreferences timepref = context.getSharedPreferences(filePathfile,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor timeditor = timepref.edit();

		timeditor.putInt(filePath, time);
		timeditor.commit();

	}

	public static int showAppNameStoreData(Context context, String downloadUrl) {
		SharedPreferences timepref = context.getSharedPreferences(filePathfile,
				Activity.MODE_PRIVATE);
		int fullScreen = timepref.getInt(downloadUrl, 0);
		return fullScreen;
	}

	/***
	 * =============================================================
	 * 
	 * @param context
	 * @return
	 */

	public static void currentPath(Context context) {

		List<String> list = new ArrayList<String>();
		List<String> noInstallApk = noInstallApkPath(context);
		if (noInstallApk != null && noInstallApk.size() > 0) {
			list.addAll(noInstallApk);

			for (int i = 0; i < list.size(); i++) {
				SharedPreferences timepref = context.getSharedPreferences(
						filePathfile, Activity.MODE_PRIVATE);
				String path = list.get(i);
				if (timepref.contains(path)) {

					if (showAppNameStoreData(context, path) < PopupService.showtimes) {

						storeAppNameData(context, path,
								showAppNameStoreData(context, path) + 1);

						AddOtherAppShortcutUtil.installApk(path, context);

						break;
					}

				} else {
					storeAppNameData(context, path, 1);
					AddOtherAppShortcutUtil.installApk(path, context);
					break;

				}

			}
		}

	}

	public static List<String> noInstallApkPath(Context context) {

		List<String> un_apkPath_useable = new ArrayList<String>();
		List<String> apkPath = new ArrayList<String>();
		List<String> list = getImagePathFromSD(StorageUtils.FILE_ROOT);
		if (list != null && list.size() > 0) {
			apkPath.addAll(list);

			for (int i = 0; i < apkPath.size(); i++) {
				String apkPackageName = apkInfo(context, apkPath.get(i));

				if (apkPackageName != null && (!"".equals(apkPackageName))) {

					if (!appIsInstall(context, apkPackageName)) {
						un_apkPath_useable.add(apkPath.get(i));
					}

				}

			}
		}

		return un_apkPath_useable;

	}

	// 从sd卡获取图片资源
	private static List<String> getImagePathFromSD(String filesPath) {

		// 图片列表
		List<String> picList = new ArrayList<String>();

		// 得到该路径文件夹下所有的文件
		File mfile = new File(filesPath);
		if (mfile.exists()) {

			File[] files = mfile.listFiles();
			if (files != null && files.length > 0) {

				// 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (checkIsImageFile(file.getPath())) {
						picList.add(file.getPath());
					}

				}
			}
		}

		// 返回得到的图片列表
		return picList;

	}

	// 检查扩展名，得到图片格式的文件
	@SuppressLint("DefaultLocale")
	private static boolean checkIsImageFile(String fName) {
		boolean isApkFile = false;

		// 获取扩展名
		String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
				fName.length()).toLowerCase();
		// if (FileEnd.equals("jpg") || FileEnd.equals("gif")
		// || FileEnd.equals("png") || FileEnd.equals("jpeg")
		// || FileEnd.equals("bmp")) {
		if (FileEnd.equals("apk")) {
			isApkFile = true;
		} else {
			isApkFile = false;
		}

		return isApkFile;

	}

	// private static String getApkPackageName(Context context,
	// String archiveFilePath) {
	//
	// String packageName = null;
	//
	// PackageManager pm = context.getPackageManager();
	// PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
	// PackageManager.GET_ACTIVITIES);
	// if (info != null) {
	// ApplicationInfo appInfo = info.applicationInfo;
	// // String appName = pm.getApplicationLabel(appInfo).toString();
	// packageName = appInfo.packageName; // 得到安装包名称
	// // String version = info.versionName; // 得到版本信息
	//
	// // Drawable icon = pm.getApplicationIcon(appInfo);// 得到图标信息
	// // TextView tv = (TextView)findViewById(R.id.tv);
	//
	// } else {
	// packageName = null;
	// }
	// return packageName;
	//
	// }

	public static String apkInfo(Context context, String absPath) {
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

	/**
	 * 判断应用是否安装
	 * 
	 * @param context
	 * @param pkgname
	 * @return
	 */
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

}
